package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.di.diaryHtmlDownloader
import com.oyetech.kmpfeatures.di.playTimerFinishedSound
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpfeatures.operator.cancelPendingSave
import com.oyetech.kmpfeatures.operator.scheduleDebouncedSave
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.entity.TagEntry
import com.oyetech.kmpmodels.postbody.DiaryQuotePutBody
import com.oyetech.kmpmodels.postbody.EntryDurationClearPostBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.postbody.TagPostBody
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryTagUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.ErrorCode
import com.oyetech.kmpmodels.ui.state.OperationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

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
    private var entrySaveJob: Job? = null
    private var timerJob: Job? = null
    private var timerStartRequested = false
    private var tagSearchDebounceJob: Job? = null
    private var tagSearchJob: Job? = null
    private var tagCreateJob: Job? = null
    private var tagContext = 0L
    private var entriesContext = 0L
    private var quoteContext = 0L
    private var listTagsContext = 0L

    init {
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun exportDiaryHtml() {
        val snapshot = state.value
        if (!snapshot.isDailyContentVisible) return
        if (snapshot.entriesOperation != OperationState.Idle ||
            snapshot.quoteOperation is OperationState.Loading ||
            (snapshot.quoteOperation is OperationState.Error && !snapshot.quoteIsDirty) ||
            snapshot.entryOperation is OperationState.Loading
        ) {
            snackbarDelegate.triggerSnackbarState(message = StringKeys.diaryExportNotReady)
            return
        }
        executeOperation(
            onStart = { this },
            operation = {
                checkNotNull(diaryHtmlDownloader) {
                    "HTML download is not supported on this platform"
                }.invoke(
                    "diary-${snapshot.selectedDate}.html",
                    diaryHtml(
                        listOf(snapshot),
                        "${StringKeys.diary} - ${snapshot.selectedDateLabel}"
                    ),
                )
            },
            onSuccess = { this },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                this
            },
        )
    }

    private fun exportDiaryArchive() {
        val snapshot = state.value
        if (!snapshot.canExportArchive) return
        if (snapshot.isEditorVisible || snapshot.entryIsDirty || snapshot.quoteIsDirty ||
            snapshot.entryOperation is OperationState.Loading ||
            snapshot.quoteOperation is OperationState.Loading
        ) {
            snackbarDelegate.triggerSnackbarState(message = StringKeys.diaryArchiveNotReady)
            return
        }
        executeOperation(
            onStart = { copy(canExportArchive = false) },
            operation = {
                val download = checkNotNull(diaryHtmlDownloader) {
                    "HTML download is not supported on this platform"
                }
                val archive = diaryEndpointOperation.getDiaryArchive()
                val entriesByDate = archive.entries.groupBy { LocalDate.parse(it.entryDate) }
                val quotesByDate = archive.quotes.associateBy { LocalDate.parse(it.date) }
                val dates = (entriesByDate.keys + quotesByDate.keys).sorted()
                check(dates.isNotEmpty()) { StringKeys.diaryArchiveEmpty }
                val days = dates.map { date ->
                    snapshot.copy(
                        selectedDate = date,
                        selectedDateLabel = formatDate(date),
                        entries = entriesByDate[date].orEmpty().map(::entryEntity),
                        dayQuote = quotesByDate[date]?.quote.orEmpty(),
                    )
                }
                download(
                    "diary-archive-${Clock.System.todayIn(TimeZone.currentSystemDefault())}.html",
                    diaryHtml(days, StringKeys.diaryArchive, archive),
                )
            },
            onSuccess = { copy(canExportArchive = true) },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(canExportArchive = true)
            },
        )
    }

    private fun completeEntry(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) return
        val entry = state.value.entries.firstOrNull { it.id == entryId } ?: return
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = {
                diaryEndpointOperation.updateEntry(
                    entryId = entryId,
                    request = EntryPostBody(
                        areaId = entry.areaId,
                        tagId = entry.tag?.id,
                        markAsCompleted = true,
                    ),
                )
            },
            onSuccess = { response ->
                val completed = entryEntity(response)
                val next = entries.map { if (it.id == entryId) completed else it }
                copy(
                    entryOperation = OperationState.Idle,
                ).withEntries(next).withEditorState()
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun startEntryTimer(entryId: String) {
        val currentState = state.value
        if (currentState.entryOperation is OperationState.Loading) {
            if (currentState.editingEntryId == entryId && entrySaveJob?.isActive == true) {
                timerStartRequested = true
            }
            return
        }
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
                    entryOperation = OperationState.Idle,
                ).withEntries(next)
                if (editingEntryId == entryId) {
                    nextState.closeEditor()
                } else {
                    nextState.withEditorState()
                }
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(message = error.message)
                copy(entryOperation = OperationState.Error(error)).withEditorState()
            },
        )
    }

    private fun cancelEntryTimer(entryId: String) {
        if (state.value.entryOperation is OperationState.Loading) {
            val saveJob = entrySaveJob
            if (saveJob?.isActive == true) {
                launch {
                    saveJob.join()
                    cancelEntryTimer(entryId)
                }
            }
            return
        }
        executeOperation(
            onStart = { copy(entryOperation = OperationState.Loading).withEditorState() },
            operation = { diaryEndpointOperation.cancelEntryTimer(entryId) },
            onSuccess = { response ->
                val updated = entryEntity(response)
                val next = entries.map { if (it.id == entryId) updated else it }
                copy(
                    entryOperation = OperationState.Idle,
                ).withEntries(next).withEditorState()
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
                delay(1000.milliseconds)
                val now = Clock.System.now()
                val finished = state.value.entries.filter {
                    it.id in runningIds && it.completedAt == null && it.timerCancelledAt == null &&
                            it.timerEndsAt?.let { end -> end <= now } == true
                }
                updateState {
                    withEntries(entries, now).withEditorState(now)
                }
                finished.forEach {
                    snackbarDelegate.triggerSnackbarState(message = StringKeys.timerFinished)
                    executeOperation(
                        onStart = { this },
                        operation = { playTimerFinishedSound() },
                        onSuccess = { this },
                        onError = { error ->
                            snackbarDelegate.triggerSnackbarState(message = error.message)
                            this
                        },
                    )
                }
                runningIds.retainAll(
                    state.value.entries.filter { timerStatus(it, now) == EntryTimerStatus.RUNNING }
                        .map { it.id }.toSet(),
                )
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

            DiaryAction.TodayClicked -> selectDay(0)
            DiaryAction.AreaMenuClicked -> updateState {
                copy(isAreaMenuVisible = !isAreaMenuVisible)
            }

            DiaryAction.AreaMenuDismissed -> updateState {
                copy(isAreaMenuVisible = false)
            }

            is DiaryAction.AreaFilterSelected -> selectAreaFilter(action.area)
            is DiaryAction.ListTagFilterSelected -> {
                val snapshot = state.value
                if (snapshot.selectedListArea == null) return
                if (action.tagId != null && snapshot.listTagOptions.none { it.id == action.tagId }) return
                updateState {
                    copy(
                        selectedListTagId = action.tagId,
                        isAllListTagsSelected = action.tagId == null,
                        listTagOptions = listTagOptions.map {
                            it.copy(isSelected = it.id == action.tagId)
                        },
                    ).withEntries(entries)
                }
            }

            DiaryAction.RetryListTagsLoadClicked -> loadListTags()
            DiaryAction.ListAllDatesClicked -> selectListDatePreset(null)
            DiaryAction.ListWeeklyClicked -> selectListDatePreset(7)
            DiaryAction.ListMonthlyClicked -> selectListDatePreset(30)
            DiaryAction.ListCustomDateRangeClicked -> {
                if (state.value.selectedListArea == null) return
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                updateState {
                    copy(
                        isDateRangeDialogVisible = true,
                        dateRangeStartInput = (listStartDate ?: today).toString(),
                        dateRangeEndInput = (listEndDate ?: today).toString(),
                        dateRangeErrorMessage = null,
                    )
                }
            }

            DiaryAction.ListDateRangeDismissed -> updateState {
                copy(isDateRangeDialogVisible = false, dateRangeErrorMessage = null)
            }

            is DiaryAction.ListDateRangeStartChanged -> updateState {
                copy(dateRangeStartInput = action.value, dateRangeErrorMessage = null)
            }

            is DiaryAction.ListDateRangeEndChanged -> updateState {
                copy(dateRangeEndInput = action.value, dateRangeErrorMessage = null)
            }

            DiaryAction.ApplyListDateRangeClicked -> applyListDateRange()

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
                cancelTagRequests()
                updateState {
                    copy(
                        selectedArea = action.value.takeUnless { it == selectedArea },
                        selectedTag = null,
                        tagQuery = "",
                        tagSuggestions = emptyList(),
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
                loadTagSuggestions()
            }

            is DiaryAction.TagQueryChanged -> {
                cancelTagRequests()
                updateState {
                    val next = copy(
                        tagQuery = action.value,
                        tagSuggestions = emptyList(),
                        tagOperation = OperationState.Loading,
                    ).withEditorState()
                    if (next.hasTagQueryError) next.copy(tagOperation = OperationState.Idle) else next
                }
                if (!state.value.hasTagQueryError) {
                    tagSearchDebounceJob = launch {
                        delay(300)
                        loadTagSuggestions()
                    }
                }
            }

            is DiaryAction.TagSelected -> {
                val snapshot = state.value
                val area = snapshot.selectedArea ?: return
                val tag = snapshot.tagSuggestions.firstOrNull { it.id == action.tagId } ?: return
                selectTag(TagEntry(tag.id, tag.name, area.name))
            }

            DiaryAction.TagCleared -> {
                cancelTagRequests()
                updateState {
                    copy(
                        selectedTag = null,
                        tagQuery = "",
                        tagSuggestions = tagSuggestions.map { it.copy(isSelected = false) },
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
                loadTagSuggestions()
            }

            DiaryAction.CreateTagClicked -> createTag()
            DiaryAction.RetryTagsLoadClicked -> loadTagSuggestions()

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
                if (action.preset == EntryTimerPreset.ALREADY_DONE) {
                    dispatch(DiaryAction.EntryCompletionChanged(true))
                    return
                }
                if (!state.value.canChangeDuration) return
                val preset = action.preset.takeUnless {
                    it == state.value.selectedTimerPreset && it != EntryTimerPreset.ALREADY_DONE
                }
                val seconds = if (preset == EntryTimerPreset.CUSTOM) {
                    customDurationSeconds(state.value.customDurationMinutes)
                } else {
                    preset?.durationSeconds
                }
                if (preset == null) timerStartRequested = false
                updateState {
                    copy(
                        selectedTimerPreset = preset,
                        customDurationMinutes = if (preset == null) "" else customDurationMinutes,
                        selectedDurationSeconds = seconds,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    ).withEditorState()
                }
                scheduleEntrySave()
            }

            is DiaryAction.EntryCompletionChanged -> {
                timerStartRequested = false
                updateState {
                    copy(
                        isEntryCompleted = action.checked,
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
            DiaryAction.ExportDiaryHtmlClicked -> exportDiaryHtml()
            DiaryAction.ExportDiaryArchiveClicked -> exportDiaryArchive()

            is DiaryAction.StartEntryTimerClicked -> startEntryTimer(action.entryId)
            is DiaryAction.CancelEntryTimerClicked -> cancelEntryTimer(action.entryId)
            DiaryAction.CancelEditorTimerClicked ->
                state.value.editingEntryId?.let(::cancelEntryTimer)

            is DiaryAction.EntryCompletedClicked -> completeEntry(action.entryId)

            is DiaryAction.EntryEditClicked -> {
                if (state.value.entryOperation is OperationState.Loading) return
                cancelTagRequests()
                timerStartRequested = false
                entryAutosaveJob.cancelPendingSave()
                val entry = state.value.entries.firstOrNull { it.id == action.entryId } ?: return
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = entry.id,
                        selectedArea = AreaEntry.values().firstOrNull { it.name == entry.areaId },
                        selectedTag = entry.tag,
                        tagQuery = "",
                        tagSuggestions = emptyList(),
                        text = entry.text,
                        selectedDurationSeconds = entry.durationSeconds,
                        selectedTimerPreset = presetFor(entry.durationSeconds),
                        isEntryCompleted = entry.completedAt != null,
                        customDurationMinutes = if (presetFor(entry.durationSeconds) == EntryTimerPreset.CUSTOM) {
                            entry.durationSeconds?.div(60)?.toString().orEmpty()
                        } else {
                            ""
                        },
                        entryIsDirty = false,
                        dismissRequested = false,
                        entryOperation = OperationState.Idle,
                    ).withEditorState()
                }
                loadTagSuggestions()
            }

            is DiaryAction.EntryDeleteClicked -> deleteEntry(action.entryId)

            DiaryAction.AddEntryClicked -> {
                if (state.value.entryOperation is OperationState.Loading) return
                cancelTagRequests()
                timerStartRequested = false
                entryAutosaveJob.cancelPendingSave()
                updateState {
                    closeEditor().copy(
                        isEditorVisible = true,
                        selectedArea = selectedListArea,
                        entryOperation = OperationState.Idle,
                    )
                        .withEditorState()
                }
                loadTagSuggestions()
            }

            DiaryAction.EntryDialogDismissed -> {
                cancelTagRequests()
                timerStartRequested = false
                val currentState = state.value
                if (currentState.entryOperation is OperationState.Loading) {
                    updateState { copy(dismissRequested = true) }
                } else if (currentState.entryOperation is OperationState.Error) {
                    dispatch(DiaryAction.EntryDialogCancelled)
                } else if (currentState.entryIsDirty) {
                    saveEntry(closeEditor = true)
                } else {
                    entryAutosaveJob.cancelPendingSave()
                    updateState { closeEditor() }
                }
            }

            DiaryAction.EntryDialogCancelled -> {
                cancelTagRequests()
                timerStartRequested = false
                entryAutosaveJob.cancelPendingSave()
                updateState { closeEditor() }
            }

            DiaryAction.SaveEntryClicked -> {
                val currentState = state.value
                if (!currentState.canCreateOrUpdateEntry) return
                val entryId = currentState.editingEntryId
                if (entryId != null && currentState.timerStatus == EntryTimerStatus.PENDING &&
                    currentState.selectedDurationSeconds != null && !currentState.isEntryCompleted
                ) {
                    startEntryTimer(entryId)
                } else {
                    saveEntry(closeEditor = true)
                }
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

    private fun cancelTagRequests() {
        tagContext++
        tagSearchDebounceJob?.cancel()
        tagSearchJob?.cancel()
        tagCreateJob?.cancel()
        updateState {
            copy(tagOperation = OperationState.Idle, tagCreateOperation = OperationState.Idle)
                .withEditorState()
        }
    }

    private fun DiaryUiState.matchesTagRequest(
        context: Long,
        area: AreaEntry,
        query: String,
    ): Boolean =
        tagContext == context && isEditorVisible && selectedArea == area && tagQuery == query

    private fun loadTagSuggestions() {
        tagSearchJob?.cancel()
        val snapshot = state.value
        val area = snapshot.selectedArea ?: return
        if (!snapshot.isEditorVisible || snapshot.hasTagQueryError) return
        val query = snapshot.tagQuery
        val context = tagContext
        tagSearchJob = executeOperation(
            onStart = { copy(tagOperation = OperationState.Loading).withEditorState() },
            operation = { diaryEndpointOperation.getAreaTags(area.name, normalizeTagName(query)) },
            onSuccess = { tags ->
                if (!matchesTagRequest(context, area, query)) this else copy(
                    tagSuggestions = tags.map {
                        DiaryTagUiState(it.id, it.name, "#${it.name}", selectedTag?.id == it.id)
                    },
                    tagOperation = OperationState.Idle,
                ).withEditorState()
            },
            onError = { error ->
                if (!matchesTagRequest(context, area, query)) this else copy(
                    tagOperation = OperationState.Error(error),
                ).withEditorState()
            },
        )
    }

    private fun selectTag(tag: TagEntry) {
        cancelTagRequests()
        updateState { withSelectedTag(tag) }
        scheduleEntrySave()
    }

    private fun createTag() {
        val snapshot = state.value
        val area = snapshot.selectedArea ?: return
        val name = normalizeTagName(snapshot.tagQuery)
        val existing = snapshot.tagSuggestions.firstOrNull {
            normalizeTagName(it.name).equals(name, ignoreCase = true)
        }
        if (existing != null) {
            selectTag(TagEntry(existing.id, existing.name, area.name))
            return
        }
        if (!snapshot.canCreateTag || tagCreateJob?.isActive == true) return
        val query = snapshot.tagQuery
        val context = tagContext
        tagCreateJob = executeOperation(
            onStart = { copy(tagCreateOperation = OperationState.Loading).withEditorState() },
            operation = { diaryEndpointOperation.createTag(TagPostBody(area.name, name)) },
            onSuccess = { response ->
                if (!matchesTagRequest(context, area, query)) this else {
                    scheduleEntrySave()
                    copy(
                        tagSuggestions = (
                                listOf(
                                    DiaryTagUiState(
                                        response.id,
                                        response.name,
                                        "#${response.name}",
                                        true
                                    )
                                ) +
                                        tagSuggestions.filterNot { it.id == response.id }
                                ).take(6),
                    ).withSelectedTag(tagEntity(response))
                }
            },
            onError = { error ->
                if (!matchesTagRequest(context, area, query)) this else copy(
                    tagCreateOperation = OperationState.Error(error),
                ).withEditorState()
            },
        )
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
        listTagsContext++
        cancelTagRequests()
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
                selectedListArea = null,
                selectedListTagId = null,
                isAllListTagsSelected = true,
                listTagOptions = emptyList(),
                listTagsOperation = OperationState.Idle,
                isAllDatesSelected = true,
                isWeeklyDateFilterSelected = false,
                isMonthlyDateFilterSelected = false,
                isCustomDateFilterSelected = false,
                listStartDate = null,
                listEndDate = null,
                isDateRangeDialogVisible = false,
                dateRangeErrorMessage = null,
                areaFilterLabel = "",
                isDailyContentVisible = true,
                isAreaMenuVisible = false,
                isAreaEntriesEmpty = false,
            )
        }
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun selectAreaFilter(area: AreaEntry) {
        if (state.value.entryOperation is OperationState.Loading) return
        cancelTagRequests()
        timerStartRequested = false
        quoteAutosaveJob.cancelPendingSave()
        entryAutosaveJob.cancelPendingSave()
        timerJob?.cancel()
        quoteContext++
        updateState {
            closeEditor().copy(
                selectedListArea = area,
                selectedListTagId = null,
                isAllListTagsSelected = true,
                listTagOptions = emptyList(),
                listTagsOperation = OperationState.Idle,
                areaFilterLabel = StringKeys.areaEntriesTitle(areaLabel(area.name)),
                isAreaMenuVisible = false,
                isDailyContentVisible = false,
                isAreaEntriesEmpty = false,
                entries = emptyList(),
                entryItems = emptyList(),
                entriesOperation = OperationState.Idle,
            ).withEntries(emptyList())
        }
        loadEntriesForSelectedDate()
        loadListTags()
    }

    private fun selectListDatePreset(days: Int?) {
        if (state.value.selectedListArea == null) return
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        updateState {
            copy(
                isAllDatesSelected = days == null,
                isWeeklyDateFilterSelected = days == 7,
                isMonthlyDateFilterSelected = days == 30,
                isCustomDateFilterSelected = false,
                listStartDate = days?.let { LocalDate.fromEpochDays(today.toEpochDays() - it + 1) },
                listEndDate = today.takeIf { days != null },
                isDateRangeDialogVisible = false,
                dateRangeErrorMessage = null,
            ).withEntries(entries)
        }
    }

    private fun applyListDateRange() {
        val snapshot = state.value
        if (snapshot.selectedListArea == null || !snapshot.isDateRangeDialogVisible) return
        val start = try {
            LocalDate.parse(snapshot.dateRangeStartInput.trim())
        } catch (_: IllegalArgumentException) {
            null
        }
        val end = try {
            LocalDate.parse(snapshot.dateRangeEndInput.trim())
        } catch (_: IllegalArgumentException) {
            null
        }
        val error = when {
            start == null || end == null -> StringKeys.invalidDateRange
            start > end -> StringKeys.reversedDateRange
            else -> null
        }
        if (error != null) {
            updateState { copy(dateRangeErrorMessage = error) }
            return
        }
        updateState {
            copy(
                isAllDatesSelected = false,
                isWeeklyDateFilterSelected = false,
                isMonthlyDateFilterSelected = false,
                isCustomDateFilterSelected = true,
                listStartDate = checkNotNull(start),
                listEndDate = checkNotNull(end),
                isDateRangeDialogVisible = false,
                dateRangeErrorMessage = null,
            ).withEntries(entries)
        }
    }

    private fun loadListTags() {
        val area = state.value.selectedListArea ?: return
        val context = ++listTagsContext
        executeOperation(
            onStart = { copy(listTagsOperation = OperationState.Loading) },
            operation = { diaryEndpointOperation.getAllAreaTags(area.name) },
            onSuccess = { tags ->
                if (context != listTagsContext || selectedListArea != area) {
                    this
                } else {
                    copy(
                        listTagOptions = tags.map {
                            DiaryTagUiState(
                                id = it.id,
                                name = it.name,
                                displayLabel = "#${it.name}",
                                isSelected = it.id == selectedListTagId,
                            )
                        },
                        listTagsOperation = OperationState.Idle,
                    ).withEntries(entries)
                }
            },
            onError = { error ->
                if (context != listTagsContext || selectedListArea != area) this else copy(
                    listTagsOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun loadEntriesForSelectedDate() {
        val context = ++entriesContext
        val dateSnapshot = state.value.selectedDate
        val areaSnapshot = state.value.selectedListArea
        executeOperation(
            onStart = {
                copy(entriesOperation = OperationState.Loading, isAreaEntriesEmpty = false)
            },
            operation = {
                if (areaSnapshot == null) {
                    diaryEndpointOperation.getEntries(dateSnapshot.toString())
                } else {
                    diaryEndpointOperation.getAreaEntries(areaSnapshot.name)
                }
            },
            onSuccess = { responses ->
                if (entriesContext != context) {
                    this
                } else {
                    val loadedEntries = responses.map(::entryEntity)
                    startTimerTicker(loadedEntries)
                    copy(
                        entriesOperation = OperationState.Idle,
                    ).withEntries(loadedEntries).withEditorState()
                }
            },
            onError = { error ->
                if (entriesContext != context) this else copy(
                    entries = emptyList(),
                    entryItems = emptyList(),
                    entriesOperation = OperationState.Error(error),
                    isAreaEntriesEmpty = false,
                )
            },
        )
    }

    private fun loadQuoteForSelectedDate() {
        val context = ++quoteContext
        val dateSnapshot = state.value.selectedDate
        executeOperation(
            onStart = {
                copy(quoteOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.getQuote(dateSnapshot.toString())
            },
            onSuccess = { response ->
                if (quoteContext != context) {
                    this
                } else if (selectedDate == dateSnapshot && !quoteIsDirty) {
                    copy(
                        dayQuote = response.quote,
                        quoteOperation = OperationState.Idle,
                    )
                } else {
                    copy(quoteOperation = OperationState.Idle)
                }
            },
            onError = { error ->
                if (quoteContext != context) return@executeOperation this
                copy(
                    dayQuote = "",
                    quoteOperation = if (error.code == ErrorCode.NotFound) {
                        OperationState.Idle
                    } else {
                        OperationState.Error(error)
                    },
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
                    request = DiaryQuotePutBody(quote = quoteSnapshot),
                )
            },
            onSuccess = {
                val nextState = if (selectedDate != dateSnapshot) {
                    copy(quoteOperation = OperationState.Idle)
                } else {
                    copy(
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
                    if (currentState.hasEntryTextError) {
                        currentState.entryTextError
                    } else if (currentState.selectedTimerPreset == EntryTimerPreset.CUSTOM) {
                        StringKeys.customDurationInvalid(MAX_CUSTOM_DURATION_MINUTES)
                    } else {
                        StringKeys.entryInputRequired
                    },
            )
            return
        }
        val revision = currentState.entryRevision
        val editingEntryId = currentState.editingEntryId
        val dateSnapshot = currentState.selectedDate
        val markAsCompleted = currentState.isEntryCompleted
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
                            tagId = currentState.selectedTag?.id,
                            text = currentState.text,
                            entryDate = dateSnapshot.toString(),
                            durationSeconds = currentState.selectedDurationSeconds,
                            markAsCompleted = markAsCompleted,
                        ),
                    )
                } else {
                    val updatedEntry = diaryEndpointOperation.updateEntry(
                        entryId = editingEntryId,
                        request = EntryPostBody(
                            areaId = currentState.selectedArea?.name,
                            tagId = currentState.selectedTag?.id,
                            text = currentState.text,
                            entryDate = existingEntry?.entryDate ?: dateSnapshot.toString(),
                            durationSeconds = currentState.selectedDurationSeconds
                                .takeUnless { it == existingEntry?.durationSeconds },
                            markAsCompleted = markAsCompleted.takeUnless {
                                it == (existingEntry?.completedAt != null)
                            },
                        ),
                    )
                    if (currentState.selectedTimerPreset == null &&
                        currentState.selectedDurationSeconds == null &&
                        existingEntry?.durationSeconds != null
                    ) {
                        diaryEndpointOperation.clearEntryDuration(
                            entryId = editingEntryId,
                            request = EntryDurationClearPostBody(durationSeconds = null),
                        )
                    } else {
                        updatedEntry
                    }
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
                    } else {
                        entries.map { if (it.id == serverEntry.id) serverEntry else it }
                    }
                    val nextState = copy(
                        editingEntryId = serverEntry.id,
                        entryIsDirty = hasNewInput,
                        entryOperation = OperationState.Idle,
                    ).withEntries(nextEntries)
                    if (!isEditorVisible || (dismissRequested && !hasNewInput)) {
                        nextState.closeEditor()
                    } else {
                        nextState.withEditorState()
                    }
                }
            },
            onError = { error ->
                if (!isEditorVisible) {
                    snackbarDelegate.triggerSnackbarState(message = error.message)
                    copy(entryOperation = OperationState.Idle).closeEditor()
                } else {
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
                }
            },
        )
        entrySaveJob = saveJob
        launch {
            saveJob.join()
            if (savedEntryId != null && state.value.selectedDate == dateSnapshot) {
                if (state.value.entryIsDirty) {
                    scheduleEntrySave()
                } else if (timerStartRequested) {
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
                    entryOperation = OperationState.Idle,
                ).withEntries(remainingEntries)
                if (editingEntryId == entryId) {
                    nextState.closeEditor()
                } else {
                    nextState.withEditorState()
                }
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
        internal fun initialState(): DiaryUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return DiaryUiState(
                selectedDate = today,
                dayOffset = 0,
                isTodaySelected = true,
                entries = emptyList(),
                selectedDateLabel = formatDate(today),
                entryItems = emptyList(),
                areaOptions = areaOptions(),
                areaOptionRows = areaOptions().chunked(3),
                timerPresetOptions = timerPresetOptions(),
                primaryButtonLabel = StringKeys.save,
                isEditorVisible = false,
                editingEntryId = null,
                selectedArea = null,
                text = "",
                dayQuote = "",
                isHtmlExportAvailable = diaryHtmlDownloader != null,
            )
        }
    }
}
