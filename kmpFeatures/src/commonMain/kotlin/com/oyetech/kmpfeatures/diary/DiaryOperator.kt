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
import com.oyetech.kmpmodels.response.DiaryArchiveResponse
import com.oyetech.kmpmodels.response.EntryResponse
import com.oyetech.kmpmodels.response.TagResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryTagUiState
import com.oyetech.kmpmodels.ui.state.DiaryTimerPresetUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.ErrorCode
import com.oyetech.kmpmodels.ui.state.OperationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
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
    private var entrySaveJob: Job? = null
    private var timerJob: Job? = null
    private var timerStartRequested = false
    private var tagSearchDebounceJob: Job? = null
    private var tagSearchJob: Job? = null
    private var tagCreateJob: Job? = null
    private var tagContext = 0L

    init {
        loadQuoteForSelectedDate()
        loadEntriesForSelectedDate()
    }

    private fun exportDiaryHtml() {
        val snapshot = state.value
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

    private fun diaryHtml(
        days: List<DiaryUiState>,
        title: String,
        archive: DiaryArchiveResponse? = null,
    ): String = buildString {
        append("<!doctype html><html lang=\"tr\"><head><meta charset=\"UTF-8\">")
        append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
        append("<title>${title.escapeHtml()}</title>")
        append(
            """<style>
                body { font-family: sans-serif; max-width: 1100px; margin: auto; padding: 24px; }
                blockquote { margin: 16px 0; padding: 12px; border-left: 3px solid; white-space: pre-wrap; overflow-wrap: anywhere; }
                table { width: 100%; border-collapse: collapse; }
                th, td { border: 1px solid #ccc; padding: 10px; text-align: left; vertical-align: top; }
                td { white-space: pre-wrap; overflow-wrap: anywhere; }
                .entries { overflow-x: auto; }
                section { margin-bottom: 40px; }
            </style></head><body>
            """.trimIndent(),
        )
        append("<h1>${title.escapeHtml()}</h1>")
        days.forEach { day ->
            append("<section><h2>${day.selectedDateLabel.escapeHtml()}</h2>")
            appendDiaryDay(day)
            append("</section>")
        }
        if (archive != null) {
            val json = Json.encodeToString(archive).replace("<", "\\u003c")
            append("<script type=\"application/json\" id=\"diary-archive-data\">$json</script>")
        }
        append("</body></html>")
    }

    private fun StringBuilder.appendDiaryDay(snapshot: DiaryUiState) {
        append("<h3>${StringKeys.todaysQuote}</h3>")
        append("<blockquote>${snapshot.dayQuote.escapeHtml()}</blockquote>")
        if (snapshot.entries.isEmpty()) {
            append("<p>${StringKeys.noDiaryEntries}</p>")
        } else {
            append("<div class=\"entries\"><table><thead><tr>")
            listOf(
                StringKeys.entryCreatedAt,
                StringKeys.timerStartedAt,
                StringKeys.area,
                StringKeys.tag,
                StringKeys.note,
                StringKeys.duration,
            ).forEach { append("<th scope=\"col\">${it.escapeHtml()}</th>") }
            append("</tr></thead><tbody>")
            snapshot.entries.sortedBy { it.createdAt }.forEach { entry ->
                append("<tr>")
                listOf(
                    entry.createdAt?.let(::exportTimestamp) ?: "-",
                    entry.timerStartedAt?.let(::exportTimestamp) ?: "-",
                    areaLabel(entry.areaId),
                    entry.tag?.name.orEmpty(),
                    entry.text,
                    entry.durationSeconds?.let(StringKeys::diaryExportDuration) ?: "-",
                ).forEach { append("<td>${it.escapeHtml()}</td>") }
                append("</tr>")
            }
            append("</tbody></table></div>")
        }
    }

    private fun exportTimestamp(instant: Instant): String {
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${local.date} ${formatTime(instant)}"
    }

    private fun String.escapeHtml(): String = replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

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
                    entries = next,
                    entryItems = renderEntryItems(next),
                    entryOperation = OperationState.Idle,
                )
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
                delay(1000.milliseconds)
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

    private fun DiaryUiState.withEditorState(now: Instant = Clock.System.now()): DiaryUiState {
        val entry = entries.firstOrNull { it.id == editingEntryId }
        val status = entry?.let { timerStatus(it, now) } ?: when {
            isEntryCompleted -> EntryTimerStatus.FINISHED
            selectedDurationSeconds != null -> EntryTimerStatus.PENDING
            else -> EntryTimerStatus.NONE
        }
        val isCustom = selectedTimerPreset == EntryTimerPreset.CUSTOM
        val validCustom = !isCustom || customDurationSeconds(customDurationMinutes) != null
        val hasContent = selectedArea != null || text.isNotBlank() ||
                selectedDurationSeconds != null || isEntryCompleted
        val validText = text.length <= MAX_ENTRY_TEXT_LENGTH
        val isSaving = entryOperation is OperationState.Loading
        val canChange = status != EntryTimerStatus.RUNNING
        val tagName = normalizeTagName(tagQuery)
        val tagNameTooLong = tagName.count { it !in '\uDC00'..'\uDFFF' } > 40
        return copy(
            isTagInputVisible = selectedArea != null,
            selectedTagLabel = selectedTag?.let { "#${it.name}" }.orEmpty(),
            canClearTag = selectedTag != null,
            hasTagQueryError = tagNameTooLong,
            tagQueryError = if (tagNameTooLong) StringKeys.tagNameTooLong else "",
            canCreateTag = isEditorVisible && selectedArea != null &&
                    tagName.isNotEmpty() && !tagNameTooLong &&
                    tagOperation !is OperationState.Loading &&
                    tagCreateOperation !is OperationState.Loading &&
                    tagSuggestions.none {
                        normalizeTagName(it.name).equals(
                            tagName,
                            ignoreCase = true
                        )
                    } &&
                    selectedTag?.name?.let {
                        normalizeTagName(it).equals(
                            tagName,
                            ignoreCase = true
                        )
                    } != true,
            createTagLabel = StringKeys.createTag(tagName),
            timerStatus = status,
            timerLabel = entry?.let { timerLabel(it, now) }.orEmpty(),
            primaryButtonLabel = if (editingEntryId != null &&
                status == EntryTimerStatus.PENDING && selectedDurationSeconds != null && !isEntryCompleted
            ) {
                StringKeys.start
            } else {
                StringKeys.save
            },
            canCreateOrUpdateEntry = hasContent && validCustom && validText,
            hasEntryTextError = !validText,
            entryTextError = if (validText) {
                ""
            } else {
                StringKeys.entryTextTooLong(
                    MAX_ENTRY_TEXT_LENGTH
                )
            },
            isCustomDurationVisible = isCustom,
            customDurationError = if (isCustom && !validCustom && customDurationMinutes.isNotEmpty()) {
                StringKeys.customDurationInvalid(MAX_CUSTOM_DURATION_MINUTES)
            } else {
                ""
            },
            hasCustomDurationError = isCustom && !validCustom && customDurationMinutes.isNotEmpty(),
            canChangeDuration = canChange,
            canCancelEditorTimer = status == EntryTimerStatus.RUNNING,
            timerPresetOptions = timerPresetOptions().map {
                it.copy(isEnabled = canChange)
            },
            canGoPrevious = !isSaving,
            canGoNext = !isSaving,
        )
    }

    private fun DiaryUiState.closeEditor(): DiaryUiState = copy(
        isEditorVisible = false,
        editingEntryId = null,
        selectedArea = null,
        selectedTag = null,
        tagQuery = "",
        tagSuggestions = emptyList(),
        tagOperation = OperationState.Idle,
        tagCreateOperation = OperationState.Idle,
        text = "",
        selectedTimerPreset = null,
        customDurationMinutes = "",
        selectedDurationSeconds = null,
        isEntryCompleted = false,
        entryIsDirty = false,
        dismissRequested = false,
        entryOperation = if (entryOperation is OperationState.Loading) {
            entryOperation
        } else {
            OperationState.Idle
        },
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
                    closeEditor().copy(isEditorVisible = true, entryOperation = OperationState.Idle)
                        .withEditorState()
                }
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

    private fun DiaryUiState.withSelectedTag(tag: TagEntry): DiaryUiState = copy(
        selectedTag = tag,
        tagQuery = "",
        tagSuggestions = tagSuggestions.map { it.copy(isSelected = it.id == tag.id) },
        entryIsDirty = true,
        entryRevision = entryRevision + 1,
        tagOperation = OperationState.Idle,
        tagCreateOperation = OperationState.Idle,
    ).withEditorState()

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
                            entryDate = dateSnapshot.toString(),
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
                        entries = nextEntries,
                        entryItems = renderEntryItems(nextEntries),
                        editingEntryId = serverEntry.id,
                        entryIsDirty = hasNewInput,
                        entryOperation = OperationState.Idle,
                    )
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
                    entries = remainingEntries,
                    entryItems = renderEntryItems(remainingEntries),
                    entryOperation = OperationState.Idle,
                )
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
        private const val MAX_CUSTOM_DURATION_MINUTES = 1440L
        private const val MAX_ENTRY_TEXT_LENGTH = 3000
        private val diaryAreas = listOf(
            AreaEntry.WORK,
            AreaEntry.BODY,
            AreaEntry.MIND,
            AreaEntry.CHARACTER,
            AreaEntry.LIFE,
        )

        private fun customDurationSeconds(minutes: String): Long? =
            minutes.takeIf { it.isNotEmpty() && it.all { char -> char in '0'..'9' } }
                ?.toLongOrNull()?.takeIf { it in 1..MAX_CUSTOM_DURATION_MINUTES }?.times(60)

        private fun presetFor(seconds: Long?): EntryTimerPreset? =
            if (seconds == null) {
                null
            } else {
                EntryTimerPreset.values()
                    .firstOrNull { it.durationSeconds == seconds } ?: EntryTimerPreset.CUSTOM
            }

        private fun timerStatus(
            entry: EntryEntity,
            now: Instant = Clock.System.now(),
        ): EntryTimerStatus = when {
            entry.timerCancelledAt != null -> EntryTimerStatus.CANCELLED
            entry.completedAt != null -> EntryTimerStatus.FINISHED
            entry.timerEndsAt?.let { it <= now } == true -> EntryTimerStatus.FINISHED
            entry.timerStartedAt != null && entry.timerEndsAt != null -> EntryTimerStatus.RUNNING
            entry.durationSeconds != null -> EntryTimerStatus.PENDING
            else -> EntryTimerStatus.NONE
        }

        private fun timerLabel(
            entry: EntryEntity,
            now: Instant = Clock.System.now(),
        ): String = when (timerStatus(entry, now)) {
            EntryTimerStatus.NONE -> ""
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

        internal fun renderEntryItems(
            entries: List<EntryEntity>,
            now: Instant = Clock.System.now(),
        ): List<DiaryEntryUiState> {
            val areas = areaOptions()
            return entries.sortedByDescending { it.createdAt }.map { entry ->
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
                    canMarkCompleted = status == EntryTimerStatus.NONE ||
                            status == EntryTimerStatus.PENDING || status == EntryTimerStatus.RUNNING,
                    isAreaVisible = entry.areaId != null,
                    tagLabel = entry.tag?.let { "#${it.name}" },
                    areaEmoji = areas.firstOrNull { it.area.name == entry.areaId }?.emoji.orEmpty(),
                    isTimerStatusVisible = status != EntryTimerStatus.NONE,
                )
            }
        }

        private fun entryEntity(response: EntryResponse): EntryEntity =
            EntryEntity(
                id = response.id,
                areaId = response.areaId,
                tag = response.tag?.let(::tagEntity),
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

        private fun tagEntity(response: TagResponse): TagEntry =
            TagEntry(response.id, response.name, response.areaId)

        private fun normalizeTagName(value: String): String =
            value.trim().replace(Regex("\\s+"), " ")

        private fun areaOptions(): List<DiaryAreaUiState> =
            diaryAreas.map { area ->
                val (emoji, description) = when (area) {
                    AreaEntry.WORK -> "🏗️" to StringKeys.workAreaDescription
                    AreaEntry.BODY -> "💪" to StringKeys.bodyAreaDescription
                    AreaEntry.MIND -> "🧠" to StringKeys.mindAreaDescription
                    AreaEntry.CHARACTER -> "🧭" to StringKeys.characterAreaDescription
                    AreaEntry.LIFE -> "🌍" to StringKeys.lifeAreaDescription
                }
                DiaryAreaUiState(
                    area = area,
                    label = areaLabel(area.name),
                    emoji = emoji,
                    description = description,
                )
            }

        private fun timerPresetOptions(): List<DiaryTimerPresetUiState> =
            EntryTimerPreset.values().filter { it != EntryTimerPreset.ALREADY_DONE }.map { preset ->
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
            null -> ""
            AreaEntry.WORK.name -> StringKeys.workArea
            AreaEntry.BODY.name -> StringKeys.bodyArea
            AreaEntry.MIND.name -> StringKeys.mindArea
            AreaEntry.CHARACTER.name -> StringKeys.characterArea
            AreaEntry.LIFE.name -> StringKeys.lifeArea
            else -> areaId
        }
    }
}
