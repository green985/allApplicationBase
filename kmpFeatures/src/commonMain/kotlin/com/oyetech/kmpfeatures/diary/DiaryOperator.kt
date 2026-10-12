package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpfeatures.operator.cancelPendingSave
import com.oyetech.kmpfeatures.operator.scheduleDebouncedSave
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.postbody.EntryPatchBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.response.EntryResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryTimerPresetUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.OperationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

class DiaryOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val diaryEndpointOperation: DiaryEndpointOperation,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseFeatureOperator<DiaryUiState, DiaryAction, Nothing>(
    initialState = initialState(),
    operatorScope = operatorScope,
) {
    private var quoteAutosaveJob: Job? = null
    private var entryAutosaveJob: Job? = null
    private var timerJob: Job? = null
    private var timerStartRequested = false

    init {
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun completeEntry(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) return
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = {
                diaryEndpointOperation.updateEntry(
                    entryId = entryId,
                    request = EntryPatchBody(markAsCompleted = true),
                )
            },
            onSuccess = { response ->
                val completed = entryEntity(response)
                val next = entries.map { if (it.id == entryId) completed else it }
                copy(
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                ).withEditorState()
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun startEntryTimer(entryId: String) {
        val currentState = state.value
        if (currentState.entryOperation is OperationState.Loading) return
        if (currentState.editingEntryId == entryId && currentState.entryIsDirty) {
            saveEntry(startTimerAfterSave = true)
            return
        }
        if (state.value.entries.any { timerStatus(it) == EntryTimerStatus.RUNNING }) {
            snackbarDelegate.triggerSnackbarState(message = StringKeys.singleTimerOnly)
            return
        }
        val entry = currentState.entries.firstOrNull { it.id == entryId } ?: return
        if (entry.durationSeconds == null || timerStatus(entry) != EntryTimerStatus.PENDING) return
        entryAutosaveJob.cancelPendingSave()
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = { diaryEndpointOperation.startEntryTimer(entryId) },
            onSuccess = { response ->
                val updated = entryEntity(response)
                val next = entries.map { if (it.id == entryId) updated else it }
                startTimerTicker(next)
                val nextState = copy(
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                )
                if (editingEntryId == entryId) nextState.closeEditor()
                else nextState.withEditorState()
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun cancelEntryTimer(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) return
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = { diaryEndpointOperation.cancelEntryTimer(entryId) },
            onSuccess = { response ->
                val updated = entryEntity(response)
                val next = entries.map { if (it.id == entryId) updated else it }
                copy(
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                ).withEditorState()
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun startTimerTicker(timerEntries: List<EntryEntity>) {
        timerJob?.cancel()
        val runningIds = timerEntries.filter { timerStatus(it) == EntryTimerStatus.RUNNING }
            .map { it.id }.toMutableSet()
        if (runningIds.isEmpty()) return
        timerJob = launch {
            while (runningIds.isNotEmpty()) {
                delay(1000)
                val now = Clock.System.now()
                val finished = state.value.entries.filter {
                    it.id in runningIds && it.completedAt == null && it.timerCancelledAt == null &&
                            it.timerEndsAt?.let { end -> end <= now } == true
                }
                updateState {
                    copy(entryItems = renderEntryItems(entries, now)).withEditorState(now)
                }
                finished.forEach {
                    snackbarDelegate.triggerSnackbarState(message = StringKeys.timerFinished)
                }
                runningIds.retainAll(
                    state.value.entries.filter { timerStatus(it, now) == EntryTimerStatus.RUNNING }
                        .map { it.id }.toSet(),
                )
            }
        }
    }

    private fun DiaryUiState.withEditorState(now: Instant = Clock.System.now()): DiaryUiState {
        val entry = entries.firstOrNull { it.id == editingEntryId }
        val status = entry?.let { timerStatus(it, now) } ?: EntryTimerStatus.PENDING
        val isCustom = selectedTimerPreset == EntryTimerPreset.CUSTOM
        val validCustom = !isCustom || customDurationSeconds(customDurationMinutes) != null
        val hasContent = selectedArea != null || text.isNotBlank() ||
                selectedDurationSeconds != null || selectedTimerPreset == EntryTimerPreset.ALREADY_DONE
        val isSaving = entryOperation is OperationState.Loading
        val canChange = status != EntryTimerStatus.RUNNING
        return copy(
            timerStatus = status,
            timerLabel = entry?.let { timerLabel(it, now) }.orEmpty(),
            primaryButtonLabel = if (editingEntryId != null &&
                status == EntryTimerStatus.PENDING && selectedDurationSeconds != null
            ) StringKeys.start else StringKeys.save,
            canCreateOrUpdateEntry = hasContent && validCustom && !isSaving,
            isCustomDurationVisible = isCustom,
            customDurationError = if (isCustom && !validCustom && customDurationMinutes.isNotEmpty()) {
                StringKeys.customDurationInvalid(MAX_CUSTOM_DURATION_MINUTES)
            } else "",
            hasCustomDurationError = isCustom && !validCustom && customDurationMinutes.isNotEmpty(),
            canChangeDuration = canChange,
            canCancelEditorTimer = status == EntryTimerStatus.RUNNING && !isSaving,
            timerPresetOptions = timerPresetOptions().map {
                it.copy(isEnabled = canChange || it.preset == EntryTimerPreset.ALREADY_DONE)
            },
            canGoPrevious = !isSaving,
            canGoNext = !isSaving,
        )
    }

    private fun DiaryUiState.closeEditor(): DiaryUiState = copy(
        isEditorVisible = false,
        editingEntryId = null,
        selectedArea = null,
        text = "",
        selectedTimerPreset = null,
        customDurationMinutes = "",
        selectedDurationSeconds = null,
        entryIsDirty = false,
        dismissRequested = false,
    ).withEditorState()

    override fun handleAction(action: DiaryAction) {
        when (action) {
            DiaryAction.PreviousDayClicked -> {
                selectDay(state.value.dayOffset - 1)
            }

            DiaryAction.NextDayClicked -> {
                selectDay(state.value.dayOffset + 1)
            }

            is DiaryAction.QuoteChanged -> {
                updateState {
                    copy(
                        dayQuote = action.value,
                        quoteIsDirty = true,
                        quoteRevision = quoteRevision + 1,
                    )

                }
                scheduleQuoteSave()
            }

            is DiaryAction.AreaSelected -> {
                updateState {
                    copy(
                        selectedArea = action.value,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
            }

            is DiaryAction.TextChanged -> {
                updateState {
                    copy(
                        text = action.value,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
            }

            is DiaryAction.TimerPresetSelected -> {
                if (!state.value.canChangeDuration && action.preset != EntryTimerPreset.ALREADY_DONE) return
                val seconds = if (action.preset == EntryTimerPreset.CUSTOM) {
                    customDurationSeconds(state.value.customDurationMinutes)
                } else action.preset.durationSeconds
                updateState {
                    copy(
                        selectedTimerPreset = action.preset,
                        selectedDurationSeconds = seconds,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
            }

            is DiaryAction.CustomDurationChanged -> {
                if (!state.value.canChangeDuration) return
                val seconds = customDurationSeconds(action.minutes)
                updateState {
                    copy(
                        customDurationMinutes = action.minutes,
                        selectedDurationSeconds = seconds,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                if (seconds != null) scheduleEntrySave()
            }

            DiaryAction.SaveEntryWithoutClosingClicked -> saveEntry()

            is DiaryAction.StartEntryTimerClicked -> startEntryTimer(action.entryId)
            is DiaryAction.CancelEntryTimerClicked -> cancelEntryTimer(action.entryId)
            DiaryAction.CancelEditorTimerClicked ->
                state.value.editingEntryId?.let(::cancelEntryTimer)
            is DiaryAction.EntryCompletedClicked -> completeEntry(action.entryId)

            is DiaryAction.EntryEditClicked -> {
                if (state.value.entryOperation is OperationState.Loading) return
                timerStartRequested = false
                entryAutosaveJob.cancelPendingSave()
                val entry = state.value.entries.firstOrNull { it.id == action.entryId } ?: return
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = entry.id,
                        selectedArea = AreaEntry.values().firstOrNull { it.name == entry.areaId },
                        text = entry.text,
                        selectedDurationSeconds = entry.durationSeconds,
                        selectedTimerPreset = if (entry.completedAt != null) {
                            EntryTimerPreset.ALREADY_DONE
                        } else presetFor(entry.durationSeconds),
                        customDurationMinutes = if (presetFor(entry.durationSeconds) == EntryTimerPreset.CUSTOM) {
                            entry.durationSeconds?.div(60)?.toString().orEmpty()
                        } else "",
                        entryIsDirty = false,
                        dismissRequested = false,
                        entryOperation = OperationState.Idle,
                    ).withEditorState()
                }
            }

            is DiaryAction.EntryDeleteClicked -> deleteEntry(action.entryId)

            DiaryAction.AddEntryClicked -> {
                if (state.value.entryOperation is OperationState.Loading) return
                timerStartRequested = false
                entryAutosaveJob.cancelPendingSave()
                updateState {
                    closeEditor().copy(isEditorVisible = true, entryOperation = OperationState.Idle)
                        .withEditorState()
                }
            }

            DiaryAction.EntryDialogDismissed -> {
                timerStartRequested = false
                val currentState = state.value
                if (currentState.entryOperation is OperationState.Loading) {
                    updateState { copy(dismissRequested = true) }
                } else if (currentState.entryIsDirty) {
                    saveEntry(closeEditor = true)
                } else {
                    entryAutosaveJob.cancelPendingSave()
                    updateState { closeEditor() }
                }
            }

            DiaryAction.SaveEntryClicked -> {
                val currentState = state.value
                if (!currentState.canCreateOrUpdateEntry) return
                val entryId = currentState.editingEntryId
                if (entryId != null && currentState.timerStatus == EntryTimerStatus.PENDING &&
                    currentState.selectedDurationSeconds != null
                ) startEntryTimer(entryId)
                else saveEntry()
            }

            DiaryAction.RetryQuoteSaveClicked -> scheduleQuoteSave()
            DiaryAction.RetryEntrySaveClicked -> saveEntry()
            DiaryAction.RetryEntriesLoadClicked -> loadEntriesForSelectedDate()
            DiaryAction.BackClicked -> navigationUseCase.goBack()
            DiaryAction.ErrorDismissed -> updateState {
                copy(
                    quoteOperation = OperationState.Idle,
                    entryOperation = OperationState.Idle,
                ).withEditorState()
            }
        }
    }

    private fun scheduleQuoteSave() {
        quoteAutosaveJob = operatorScope.scheduleDebouncedSave(
            previousJob = quoteAutosaveJob,
            canSave = {
                val currentState = state.value
                currentState.quoteIsDirty &&
                        currentState.quoteOperation !is OperationState.Loading &&
                        currentState.dayQuote.isNotBlank()
            },
            onSave = { saveQuoteSnapshot() },
        )
    }

    private fun selectDay(offset: Int) {
        if (state.value.entryOperation is OperationState.Loading) return
        timerStartRequested = false
        quoteAutosaveJob.cancelPendingSave()
        entryAutosaveJob.cancelPendingSave()
        timerJob?.cancel()
        updateState {
            stateFor(offset).closeEditor().copy(
                dayQuote = "",
                quoteIsDirty = false,
                quoteOperation = OperationState.Idle,
                entries = emptyList(),
                entryItems = emptyList(),
                entriesOperation = OperationState.Idle,
            )
        }
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun loadEntriesForSelectedDate() {
        val dateSnapshot = state.value.selectedDate
        executeOperation(
            onStart = {
                copy(entriesOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.getEntries(dateSnapshot.toString())
            },
            onSuccess = { responses ->
                if (selectedDate != dateSnapshot) {
                    copy(entriesOperation = OperationState.Idle)
                } else {
                    val loadedEntries = responses.map(::entryEntity)
                    startTimerTicker(loadedEntries)
                    copy(
                        entries = loadedEntries,
                        entryItems = renderEntryItems(loadedEntries),
                        entriesOperation = OperationState.Idle,
                    ).withEditorState()
                }
            },
            onError = { error ->
                copy(
                    entries = emptyList(),
                    entryItems = emptyList(),
                    entriesOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun loadQuoteForSelectedDate() {
        val dateSnapshot = state.value.selectedDate
        executeOperation(
            onStart = {
                copy(quoteOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.getQuote(dateSnapshot.toString())
            },
            onSuccess = { response ->
                if (selectedDate == dateSnapshot && !quoteIsDirty) {
                    copy(
                        dayQuote = response.quote,
                        quoteOperation = OperationState.Idle,
                    )
                } else {
                    copy(quoteOperation = OperationState.Idle)
                }
            },
            onError = { error ->
                copy(
                    dayQuote = "",
                    quoteOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun saveQuoteSnapshot() {
        quoteAutosaveJob.cancelPendingSave()
        val snapshot = state.value
        val revision = snapshot.quoteRevision
        val quoteSnapshot = snapshot.dayQuote
        val dateSnapshot = snapshot.selectedDate

        executeOperation(
            onStart = {
                copy(quoteOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.updateQuote(
                    date = dateSnapshot.toString(),
                    quote = quoteSnapshot,
                )
            },
            onSuccess = { response ->
                val nextState = if (selectedDate != dateSnapshot) {
                    copy(quoteOperation = OperationState.Idle)
                } else {
                    copy(
                        dayQuote = if (dayQuote == quoteSnapshot) response.quote else dayQuote,
                        quoteIsDirty = quoteRevision != revision,
                        quoteOperation = OperationState.Idle,
                    )
                }
                if (quoteRevision != revision) scheduleQuoteSave()
                nextState
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                    actionLabel = "Retry",
                    onAction = { dispatch(DiaryAction.RetryQuoteSaveClicked) },
                )
                copy(
                    quoteIsDirty = true,
                    quoteOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun scheduleEntrySave() {
        entryAutosaveJob = operatorScope.scheduleDebouncedSave(
            previousJob = entryAutosaveJob,
            canSave = {
                val currentState = state.value
                currentState.entryIsDirty &&
                        currentState.entryOperation !is OperationState.Loading &&
                        currentState.isEditorVisible &&
                        currentState.canCreateOrUpdateEntry
            },
            onSave = { saveEntry() },
        )
    }

    private fun saveEntry(
        closeEditor: Boolean = false,
        startTimerAfterSave: Boolean = false,
    ) {
        val currentState = state.value
        if (startTimerAfterSave) timerStartRequested = true
        if (currentState.entryOperation is OperationState.Loading) {
            if (closeEditor) updateState { copy(dismissRequested = true) }
            return
        }

        entryAutosaveJob.cancelPendingSave()
        if (!currentState.canCreateOrUpdateEntry) {
            snackbarDelegate.triggerSnackbarState(
                message =
                    if (currentState.selectedTimerPreset == EntryTimerPreset.CUSTOM) {
                        StringKeys.customDurationInvalid(MAX_CUSTOM_DURATION_MINUTES)
                    } else StringKeys.entryInputRequired,
            )
            return
        }
        val revision = currentState.entryRevision
        val editingEntryId = currentState.editingEntryId
        val dateSnapshot = currentState.selectedDate
        val markAsCompleted = currentState.selectedTimerPreset == EntryTimerPreset.ALREADY_DONE
        val existingEntry = currentState.entries.firstOrNull { it.id == editingEntryId }
        var savedEntryId: String? = null
        val saveJob = executeOperation(
            onStart = {
                copy(
                    entryOperation = OperationState.Loading,
                    dismissRequested = dismissRequested || closeEditor,
                ).withEditorState()
            },
            operation = {
                if (editingEntryId == null) {
                    diaryEndpointOperation.createEntry(
                        EntryPostBody(
                            areaId = currentState.selectedArea?.name,
                            text = currentState.text,
                            entryDate = dateSnapshot.toString(),
                            durationSeconds = currentState.selectedDurationSeconds,
                            markAsCompleted = markAsCompleted,
                        ),
                    )
                } else {
                    diaryEndpointOperation.updateEntry(
                        entryId = editingEntryId,
                        request = EntryPatchBody(
                            areaId = currentState.selectedArea?.name,
                            text = currentState.text,
                            entryDate = dateSnapshot.toString(),
                            durationSeconds = currentState.selectedDurationSeconds
                                .takeUnless { it == existingEntry?.durationSeconds },
                            markAsCompleted = markAsCompleted,
                        ),
                    )
                }
            },
            onSuccess = { response ->
                val serverEntry = entryEntity(response)
                savedEntryId = serverEntry.id
                if (selectedDate != dateSnapshot) {
                    this
                } else {
                    val hasNewInput = entryRevision != revision
                    val nextEntries = if (editingEntryId == null) {
                        listOf(serverEntry) + entries.filterNot { it.id == serverEntry.id }
                    } else entries.map { if (it.id == serverEntry.id) serverEntry else it }
                    val nextState = copy(
                        entries = nextEntries,
                        entryItems = renderEntryItems(nextEntries),
                        editingEntryId = serverEntry.id,
                        entryIsDirty = hasNewInput,
                        entryOperation = OperationState.Idle,
                    )
                    if (dismissRequested && !hasNewInput) nextState.closeEditor()
                    else nextState.withEditorState()
                }
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                    actionLabel = "Retry",
                    onAction = { dispatch(DiaryAction.RetryEntrySaveClicked) },
                )
                copy(
                    entryIsDirty = true,
                    entryOperation = OperationState.Error(error),
                    dismissRequested = false,
                ).withEditorState()
            },
        )
        launch {
            saveJob.join()
            if (savedEntryId != null && state.value.selectedDate == dateSnapshot) {
                if (state.value.entryIsDirty) scheduleEntrySave()
                else if (timerStartRequested) {
                    timerStartRequested = false
                    savedEntryId?.let(::startEntryTimer)
                }
            }
        }
    }

    private fun deleteEntry(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) {
            return
        }

        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = {
                diaryEndpointOperation.deleteEntry(entryId)
            },
            onSuccess = {
                val remainingEntries = entries.filterNot { it.id == entryId }
                val nextState = copy(
                    entries = remainingEntries,
                    entryItems = renderEntryItems(remainingEntries),
                    entryOperation = OperationState.Idle,
                )
                if (editingEntryId == entryId) nextState.closeEditor()
                else nextState.withEditorState()
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                )
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun stateFor(offset: Int): DiaryUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + offset)
        return state.value.copy(
            selectedDate = selectedDate,
            dayOffset = offset,
            isTodaySelected = selectedDate == today,
            selectedDateLabel = formatDate(selectedDate),
        )
    }

    companion object {
        private const val MAX_CUSTOM_DURATION_MINUTES = 1440L

        private fun customDurationSeconds(minutes: String): Long? =
            minutes.takeIf { it.isNotEmpty() && it.all { char -> char in '0'..'9' } }
                ?.toLongOrNull()?.takeIf { it in 1..MAX_CUSTOM_DURATION_MINUTES }?.times(60)

        private fun presetFor(seconds: Long?): EntryTimerPreset? =
            if (seconds == null) null else EntryTimerPreset.values()
                .firstOrNull { it.durationSeconds == seconds } ?: EntryTimerPreset.CUSTOM

        private fun timerStatus(
            entry: EntryEntity,
            now: Instant = Clock.System.now(),
        ): EntryTimerStatus = when {
            entry.timerCancelledAt != null -> EntryTimerStatus.CANCELLED
            entry.completedAt != null -> EntryTimerStatus.FINISHED
            entry.timerEndsAt?.let { it <= now } == true -> EntryTimerStatus.FINISHED
            entry.timerStartedAt != null && entry.timerEndsAt != null -> EntryTimerStatus.RUNNING
            entry.durationSeconds != null -> EntryTimerStatus.PENDING
            else -> EntryTimerStatus.PENDING
        }

        private fun timerLabel(
            entry: EntryEntity,
            now: Instant = Clock.System.now(),
        ): String = when (timerStatus(entry, now)) {
            EntryTimerStatus.PENDING -> StringKeys.pending
            EntryTimerStatus.RUNNING -> entry.timerEndsAt?.let { remainingLabel(it, now) }
                ?: StringKeys.pending

            EntryTimerStatus.FINISHED -> StringKeys.finished
            EntryTimerStatus.CANCELLED -> StringKeys.cancelled
        }

        private fun remainingLabel(end: Instant, now: Instant): String {
            val seconds = (end - now).inWholeSeconds.coerceAtLeast(0)
            return "${(seconds / 60).toString().padStart(2, '0')}:${
                (seconds % 60).toString().padStart(2, '0')
            }"
        }

        private fun initialState(): DiaryUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return DiaryUiState(
                selectedDate = today,
                dayOffset = 0,
                isTodaySelected = true,
                entries = emptyList(),
                selectedDateLabel = formatDate(today),
                entryItems = emptyList(),
                areaOptions = areaOptions(),
                areaOptionRows = areaOptions().chunked(4),
                timerPresetOptions = timerPresetOptions(),
                primaryButtonLabel = StringKeys.save,
                isEditorVisible = false,
                editingEntryId = null,
                selectedArea = null,
                text = "",
                dayQuote = "",
            )
        }

        internal fun renderEntryItems(
            entries: List<EntryEntity>,
            now: Instant = Clock.System.now(),
        ): List<DiaryEntryUiState> =
            entries.sortedByDescending { it.createdAt }.map { entry ->
                val status = timerStatus(entry, now)
                val label = timerLabel(entry, now)
                DiaryEntryUiState(
                    id = entry.id,
                    areaLabel = areaLabel(entry.areaId),
                    createdBy = entry.createdBy,
                    timeLabel = entry.createdAt?.let(::formatTime).orEmpty(),
                    text = entry.text,
                    timerStatusLabel = label,
                    countdownLabel = if (status == EntryTimerStatus.RUNNING) label else null,
                    isTimerRunning = status == EntryTimerStatus.RUNNING,
                    canStartTimer = status == EntryTimerStatus.PENDING && entry.durationSeconds != null,
                    canCancelTimer = status == EntryTimerStatus.RUNNING,
                    canMarkCompleted = status == EntryTimerStatus.PENDING || status == EntryTimerStatus.RUNNING,
                )
            }

        private fun entryEntity(response: EntryResponse): EntryEntity =
            EntryEntity(
                id = response.id,
                areaId = response.areaId,
                text = response.text,
                entryDate = response.entryDate,
                durationSeconds = response.durationSeconds,
                timerStartedAt = response.timerStartedAt?.let(Instant::parse),
                timerEndsAt = response.timerEndsAt?.let(Instant::parse),
                timerCancelledAt = response.timerCancelledAt?.let(Instant::parse),
                completedAt = response.completedAt?.let(Instant::parse),
                createdBy = response.createdBy.orEmpty(),
                note = response.note,
                createdAt = response.createdAt?.let(Instant::parse),
                updatedAt = response.updatedAt?.let(Instant::parse),
            )

        private fun areaOptions(): List<DiaryAreaUiState> =
            AreaEntry.values().map { area ->
                DiaryAreaUiState(
                    area = area,
                    label = areaLabel(area.name),
                )
            }

        private fun timerPresetOptions(): List<DiaryTimerPresetUiState> =
            EntryTimerPreset.values().map { preset ->
                DiaryTimerPresetUiState(
                    preset = preset,
                    label = when (preset) {
                        EntryTimerPreset.ALREADY_DONE -> StringKeys.alreadyDone
                        EntryTimerPreset.FIVE_MINUTES -> StringKeys.fiveMinutes
                        EntryTimerPreset.TEN_MINUTES -> StringKeys.tenMinutes
                        EntryTimerPreset.FIFTEEN_MINUTES -> StringKeys.fifteenMinutes
                        EntryTimerPreset.TWENTY_MINUTES -> StringKeys.twentyMinutes
                        EntryTimerPreset.CUSTOM -> StringKeys.custom
                    },
                )
            }

        private fun formatDate(date: LocalDate): String =
            "${date.day} ${StringKeys.turkishMonths[date.monthNumber - 1]} ${date.year}"

        private fun formatTime(instant: kotlin.time.Instant): String {
            val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time
            return "${localTime.hour.toString().padStart(2, '0')}:${
                localTime.minute.toString().padStart(2, '0')
            }"
        }

        private fun areaLabel(areaId: String?): String = when (areaId) {
            null -> StringKeys.idleArea
            AreaEntry.IDLE.name -> StringKeys.idleArea
            AreaEntry.WORK.name -> StringKeys.workArea
            AreaEntry.BODY.name -> StringKeys.bodyArea
            AreaEntry.HEALTH.name -> StringKeys.healthArea
            AreaEntry.MIND.name -> StringKeys.mindArea
            AreaEntry.CHARACTER.name -> StringKeys.characterArea
            AreaEntry.PEOPLE.name -> StringKeys.peopleArea
            AreaEntry.LIFE.name -> StringKeys.lifeArea
            else -> areaId
        }
    }
}
