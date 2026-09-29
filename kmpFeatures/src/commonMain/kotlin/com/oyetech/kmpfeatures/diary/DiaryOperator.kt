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

    init {
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun completeEntry(entryId: String) {
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading) },
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
                )
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error))
            },
        )
    }

    private fun startEntryTimer(entryId: String) {
        if (state.value.entries.any { timerStatus(it) == EntryTimerStatus.RUNNING }) {
            snackbarDelegate.triggerSnackbarState(message = "Aynı anda yalnız bir timer çalışabilir.")
            return
        }
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading) },
            operation = { diaryEndpointOperation.startEntryTimer(entryId) },
            onSuccess = { response ->
                val updated = entryEntity(response)
                val next = entries.map { if (it.id == entryId) updated else it }
                startTimerTicker()
                copy(
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                    isEditorVisible = false,
                    editingEntryId = null,
                )
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error))
            },
        )
    }

    private fun cancelEntryTimer(entryId: String) {
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading) },
            operation = { diaryEndpointOperation.cancelEntryTimer(entryId) },
            onSuccess = { response ->
                val updated = entryEntity(response)
                val next = entries.map { if (it.id == entryId) updated else it }
                copy(
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                )
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error))
            },
        )
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = launch {
            while (true) {
                delay(1000)
                val running =
                    state.value.entries.any { timerStatus(it) == EntryTimerStatus.RUNNING }
                updateState {
                    copy(entryItems = renderEntryItems(entries))
                }
                if (!running) break
            }
        }
    }

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
                    )
                }
                scheduleEntrySave()
            }

            is DiaryAction.TextChanged -> {
                updateState {
                    copy(
                        text = action.value,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    )
                }
                scheduleEntrySave()
            }

            is DiaryAction.TimerPresetSelected -> {
                val seconds = action.preset.durationSeconds
                updateState {
                    copy(
                        selectedTimerPreset = action.preset,
                        selectedDurationSeconds = seconds,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                        canCreateOrUpdateEntry = true,
                        primaryButtonLabel = if (editingEntryId == null) StringKeys.save else StringKeys.start,
                    )
                }
                scheduleEntrySave()
            }

            is DiaryAction.CustomDurationChanged -> {
                val minutes = action.minutes.toLongOrNull()
                val seconds = minutes?.takeIf { it in 1..1440 }?.times(60)
                updateState {
                    copy(
                        customDurationMinutes = action.minutes,
                        selectedDurationSeconds = seconds,
                        entryIsDirty = seconds != null,
                        entryRevision = entryRevision + 1,
                        canCreateOrUpdateEntry = seconds != null,
                    )
                }
                if (seconds != null) scheduleEntrySave()
            }

            DiaryAction.SaveEntryWithoutClosingClicked -> saveEntry(
                closeEditor = false,
                allowIdleArea = true
            )

            is DiaryAction.StartEntryTimerClicked -> startEntryTimer(action.entryId)
            is DiaryAction.CancelEntryTimerClicked -> cancelEntryTimer(action.entryId)
            is DiaryAction.EntryCompletedClicked -> completeEntry(action.entryId)

            is DiaryAction.EntryEditClicked -> {
                val entry = state.value.entries.firstOrNull { it.id == action.entryId } ?: return
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = entry.id,
                        selectedArea = entry.areaId
                            ?.let { areaId -> AreaEntry.entries.firstOrNull { it.name == areaId } },
                        text = entry.text,
                        selectedDurationSeconds = entry.durationSeconds,
                        selectedTimerPreset = presetFor(entry.durationSeconds),
                        timerStatus = timerStatus(entry).name,
                        timerLabel = timerLabel(entry),
                        primaryButtonLabel = if (entry.durationSeconds != null) StringKeys.start else StringKeys.save,
                        canCreateOrUpdateEntry = true,
                        entryIsDirty = false,
                    )
                }
            }

            is DiaryAction.EntryDeleteClicked -> deleteEntry(action.entryId)

            DiaryAction.AddEntryClicked -> {
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = null,
                        selectedArea = null,
                        text = "",
                        selectedTimerPreset = null,
                        customDurationMinutes = "",
                        selectedDurationSeconds = null,
                        timerStatus = "",
                        timerLabel = "",
                        primaryButtonLabel = StringKeys.save,
                        canCreateOrUpdateEntry = false,
                        entryIsDirty = false,
                    )
                }
            }

            DiaryAction.EntryDialogDismissed -> {
                val currentState = state.value
                if (currentState.selectedArea == null && currentState.text.isNotBlank()) {
                    snackbarDelegate.triggerSnackbarState(
                        message = StringKeys.selectAreaRequired,
                    )
                } else if (currentState.selectedArea != null) {
                    saveEntry(closeEditor = true, allowIdleArea = false)
                } else {
                    updateState {
                        copy(
                            isEditorVisible = false,
                            editingEntryId = null,
                            selectedArea = null,
                            text = "",
                            entryIsDirty = false,
                            entryOperation = OperationState.Idle,
                        )
                    }
                }
            }

            DiaryAction.SaveEntryClicked -> saveEntry(
                closeEditor = true,
                allowIdleArea = false,
            )
            DiaryAction.RetryQuoteSaveClicked -> scheduleQuoteSave()
            DiaryAction.RetryEntrySaveClicked -> scheduleEntrySave()
            DiaryAction.RetryEntriesLoadClicked -> loadEntriesForSelectedDate()
            DiaryAction.BackClicked -> navigationUseCase.goBack()
            DiaryAction.ErrorDismissed -> updateState {
                copy(
                    quoteOperation = OperationState.Idle,
                    entryOperation = OperationState.Idle,
                )
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
        quoteAutosaveJob.cancelPendingSave()
        updateState {
            stateFor(offset).copy(
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
                    copy(
                        entries = loadedEntries,
                        entryItems = renderEntryItems(loadedEntries),
                        entriesOperation = OperationState.Idle,
                    )
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
                        (currentState.selectedArea != null || currentState.text.isNotBlank())
            },
            onSave = { saveEntry(closeEditor = false, allowIdleArea = true) },
        )
    }

    private fun saveEntry(
        closeEditor: Boolean,
        allowIdleArea: Boolean,
    ) {
        val currentState = state.value
        val area = currentState.selectedArea
        if (currentState.entryOperation is OperationState.Loading) {
            return
        }

        entryAutosaveJob.cancelPendingSave()
        val text = currentState.text.trim()
        if (area == null && !allowIdleArea) {
            snackbarDelegate.triggerSnackbarState(
                message = StringKeys.selectAreaRequired,
            )
            return
        }
        if (area == null && text.isBlank()) {
            return
        }
        val areaForRequest = area?.name ?: if (currentState.selectedDurationSeconds == null) {
            AreaEntry.IDLE.name
        } else {
            null
        }
        val revision = currentState.entryRevision
        val editingEntryId = currentState.editingEntryId
        val dateSnapshot = currentState.selectedDate

        if (editingEntryId == null) {
            executeOperation(
                onStart = {
                    copy(entryOperation = OperationState.Loading)
                },
                operation = {
                    diaryEndpointOperation.createEntry(
                        EntryPostBody(
                            areaId = areaForRequest,
                            text = text,
                            entryDate = dateSnapshot.toString(),
                            durationSeconds = currentState.selectedDurationSeconds,
                        ),
                    )
                },
                onSuccess = { response ->
                    val hasNewInput = entryRevision != revision
                    if (selectedDate != dateSnapshot) {
                        copy(entryOperation = OperationState.Idle)
                    } else {
                        val serverEntry = entryEntity(response)
                        val nextEntries = listOf(serverEntry) + entries
                        val shouldCloseEditor = closeEditor && !hasNewInput
                        if (hasNewInput) scheduleEntrySave()
                        copy(
                            entries = nextEntries,
                            entryItems = renderEntryItems(nextEntries),
                            editingEntryId = response.id,
                            entryIsDirty = hasNewInput,
                            entryOperation = OperationState.Idle,
                            isEditorVisible = !shouldCloseEditor,
                            selectedArea = if (shouldCloseEditor) null else selectedArea,
                            text = if (shouldCloseEditor) "" else this.text,
                            selectedDurationSeconds = if (shouldCloseEditor) null else selectedDurationSeconds,
                        )
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
                    )
                },
            )
            return
        }

        val updatedEntries = currentState.entries.map { entry ->
            if (entry.id == editingEntryId) {
                entry.copy(
                    areaId = areaForRequest ?: entry.areaId,
                    text = text,
                    updatedAt = Clock.System.now(),
                )
            } else {
                entry
            }
        }
        updateState {
            copy(
                entries = updatedEntries,
                entryItems = renderEntryItems(updatedEntries),
                isEditorVisible = if (closeEditor) false else currentState.isEditorVisible,
                editingEntryId = if (closeEditor) null else editingEntryId,
                selectedArea = if (closeEditor) null else area,
                text = if (closeEditor) "" else text,
                entryOperation = OperationState.Loading,
            )
        }
        executeOperation(
            onStart = {
                copy(entryOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.updateEntry(
                    entryId = editingEntryId,
                    request = EntryPatchBody(
                        areaId = areaForRequest,
                        text = text,
                        entryDate = dateSnapshot.toString(),
                        durationSeconds = currentState.selectedDurationSeconds,
                    ),
                )
            },
            onSuccess = {
                if (entryRevision != revision) scheduleEntrySave()
                copy(
                    entryIsDirty = entryRevision != revision,
                    entryOperation = OperationState.Idle,
                )
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
                )
            },
        )
    }

    private fun deleteEntry(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) {
            return
        }

        executeOperation(
            onStart = {
                copy(entryOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.deleteEntry(entryId)
            },
            onSuccess = {
                val remainingEntries = entries.filterNot { it.id == entryId }
                copy(
                    entries = remainingEntries,
                    entryItems = renderEntryItems(remainingEntries),
                    isEditorVisible = if (editingEntryId == entryId) false else isEditorVisible,
                    editingEntryId = if (editingEntryId == entryId) null else editingEntryId,
                    selectedArea = if (editingEntryId == entryId) null else selectedArea,
                    text = if (editingEntryId == entryId) "" else text,
                    entryOperation = OperationState.Idle,
                )
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                )
                copy(entryOperation = OperationState.Error(error))
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
        private fun presetFor(seconds: Long?): EntryTimerPreset? = when (seconds) {
            300L -> EntryTimerPreset.FIVE_MINUTES
            600L -> EntryTimerPreset.TEN_MINUTES
            900L -> EntryTimerPreset.FIFTEEN_MINUTES
            1200L -> EntryTimerPreset.TWENTY_MINUTES
            null -> null
            else -> EntryTimerPreset.CUSTOM
        }

        private fun timerStatus(entry: EntryEntity): EntryTimerStatus = when {
            entry.timerCancelledAt != null -> EntryTimerStatus.CANCELLED
            entry.completedAt != null -> EntryTimerStatus.FINISHED
            entry.timerEndsAt?.let { it <= Clock.System.now() } == true -> EntryTimerStatus.FINISHED
            entry.timerStartedAt != null && entry.timerEndsAt != null -> EntryTimerStatus.RUNNING
            entry.durationSeconds != null -> EntryTimerStatus.PENDING
            else -> EntryTimerStatus.FINISHED
        }

        private fun timerLabel(entry: EntryEntity): String = when (timerStatus(entry)) {
            EntryTimerStatus.PENDING -> StringKeys.pending
            EntryTimerStatus.RUNNING -> entry.timerEndsAt?.let(::remainingLabel)
                ?: StringKeys.pending

            EntryTimerStatus.FINISHED -> StringKeys.finished
            EntryTimerStatus.CANCELLED -> StringKeys.cancelled
        }

        private fun remainingLabel(end: Instant): String {
            val seconds = ((end - Clock.System.now()).inWholeSeconds).coerceAtLeast(0)
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
                isEditorVisible = false,
                editingEntryId = null,
                selectedArea = null,
                text = "",
                dayQuote = "",
            )
        }

        private fun renderEntryItems(entries: List<EntryEntity>): List<DiaryEntryUiState> =
            entries.map { entry ->
                DiaryEntryUiState(
                    id = entry.id,
                    areaLabel = areaLabel(entry.areaId),
                    createdBy = entry.createdBy,
                    timeLabel = entry.createdAt?.let(::formatTime).orEmpty(),
                    text = entry.text,
                    timerStatusLabel = timerLabel(entry),
                    countdownLabel = if (timerStatus(entry) == EntryTimerStatus.RUNNING) timerLabel(
                        entry
                    ) else null,
                    isTimerRunning = timerStatus(entry) == EntryTimerStatus.RUNNING,
                    canStartTimer = timerStatus(entry) == EntryTimerStatus.PENDING,
                    canCancelTimer = timerStatus(entry) == EntryTimerStatus.RUNNING,
                    canMarkCompleted = entry.completedAt == null,
                )
            }

        private fun entryEntity(response: EntryResponse): EntryEntity =
            EntryEntity(
                id = response.id,
                areaId = response.areaId.orEmpty(),
                text = response.text,
                entryDate = response.entryDate,
                durationSeconds = response.durationSeconds,
                timerStartedAt = response.timerStartedAt?.let(Instant::parse),
                timerEndsAt = response.timerEndsAt?.let(Instant::parse),
                timerCancelledAt = response.timerCancelledAt?.let(Instant::parse),
                completedAt = response.completedAt?.let(Instant::parse),
                createdBy = response.createdBy.orEmpty(),
                createdAt = response.createdAt?.let(Instant::parse),
                updatedAt = response.updatedAt?.let(Instant::parse),
            )

        private fun areaOptions(): List<DiaryAreaUiState> =
            AreaEntry.entries.map { area ->
                DiaryAreaUiState(
                    area = area,
                    label = areaLabel(area.name),
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

        private fun areaLabel(areaId: String): String = when (areaId) {
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
