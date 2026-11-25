package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.entity.TagEntry
import com.oyetech.kmpmodels.postbody.MAX_ENTRY_TEXT_LENGTH
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.DiaryTagUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.OperationState
import kotlin.time.Clock
import kotlin.time.Instant

internal fun DiaryUiState.withEntries(
    next: List<EntryEntity>,
    now: Instant = Clock.System.now(),
): DiaryUiState {
    val items = renderEntryItems(
        next, now, selectedListArea, selectedListTagId, listStartDate, listEndDate,
    )
    val entryTags = next.filter { it.areaId == selectedListArea?.name }
        .mapNotNull { it.tag }
        .map { tag ->
            DiaryTagUiState(
                id = tag.id,
                name = tag.name,
                displayLabel = "#${tag.name}",
                isSelected = tag.id == selectedListTagId,
            )
        }
    return copy(
        entries = next,
        entryItems = items,
        areaFilterLabel = selectedListArea?.let {
            StringKeys.areaEntriesTitle(
                areaLabel(it.name),
                when {
                    isWeeklyDateFilterSelected -> StringKeys.lastSevenDays
                    isMonthlyDateFilterSelected -> StringKeys.lastThirtyDays
                    isCustomDateFilterSelected -> "${formatDate(checkNotNull(listStartDate))} – ${
                        formatDate(checkNotNull(listEndDate))
                    }"

                    else -> StringKeys.allDates
                },
            )
        }.orEmpty(),
        listTagOptions = (listTagOptions + entryTags).distinctBy { it.id }
            .sortedBy { it.name.lowercase() },
        areaEntriesEmptyMessage = when {
            !isAllDatesSelected -> StringKeys.noFilteredAreaEntries
            selectedListTagId == null -> StringKeys.noAreaEntries
            else -> StringKeys.noAreaTagEntries
        },
        isAreaEntriesEmpty = selectedListArea != null && items.isEmpty() &&
                entriesOperation == OperationState.Idle,
    )
}

internal fun DiaryUiState.withReminderEntries(next: List<EntryEntity>): DiaryUiState =
    copy(
        reminderEntries = next,
        reminderItems = renderEntryItems(next, showDates = true),
        isReminderListEmpty = next.isEmpty(),
    )

internal fun DiaryUiState.withSavedEntry(entry: EntryEntity): DiaryUiState {
    val matchesList = selectedListArea?.let { it.name == entry.areaId }
        ?: (entry.entryDate == selectedDate.toString())
    val normal = entries.filterNot { it.id == entry.id }
    val reminders = reminderEntries.filterNot { it.id == entry.id }
    return withEntries(if (!entry.isReminder && matchesList) normal + entry else normal)
        .withReminderEntries(if (entry.isReminder) reminders + entry else reminders)
        .let { if (editingEntryId == entry.id) it.copy(editedEntry = entry) else it }
}

internal fun DiaryUiState.withEditorState(now: Instant = Clock.System.now()): DiaryUiState {
    val entry = (entries + reminderEntries).firstOrNull { it.id == editingEntryId } ?: editedEntry
    val status = entry?.let { timerStatus(it, now) } ?: when {
        isEntryCompleted -> EntryTimerStatus.FINISHED
        selectedDurationSeconds != null -> EntryTimerStatus.PENDING
        else -> EntryTimerStatus.NONE
    }
    val isCustom = selectedTimerPreset == EntryTimerPreset.CUSTOM
    val validCustom = !isCustom || customDurationSeconds(customDurationMinutes) != null
    val hasContent = selectedArea != null || text.isNotBlank() ||
            selectedDurationSeconds != null || isEntryCompleted
    val validText = text.count { it !in '\uDC00'..'\uDFFF' } <= MAX_ENTRY_TEXT_LENGTH
    val isSaving = entryOperation is OperationState.Loading
    val canChange = status != EntryTimerStatus.RUNNING
    val canStart = editingEntryId != null && status == EntryTimerStatus.PENDING &&
            selectedDurationSeconds != null && !isEntryCompleted && !isEntryReminder && entry?.isReminder != true
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
        primaryButtonLabel = if (canStart) {
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
        canCancelEditorTimer = !isEntryReminder && status == EntryTimerStatus.RUNNING,
        canStartEditorTimer = canStart,
        canToggleEntryReminder = isBulkEntryAvailable && canChange && !isSaving,
        timerPresetOptions = timerPresetOptions().map {
            it.copy(isEnabled = canChange)
        },
        canGoPrevious = !isSaving,
        canGoNext = !isSaving,
    )
}

internal fun DiaryUiState.closeEditor(): DiaryUiState = copy(
    isEditorVisible = false,
    editingEntryId = null,
    editedEntry = null,
    isEntryReminder = false,
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

internal fun DiaryUiState.withSelectedTag(tag: TagEntry): DiaryUiState = copy(
    selectedTag = tag,
    tagQuery = "",
    tagSuggestions = tagSuggestions.map { it.copy(isSelected = it.id == tag.id) },
    listTagOptions = if (tag.areaId == selectedListArea?.name) {
        (
                listTagOptions + DiaryTagUiState(
                    id = tag.id,
                    name = tag.name,
                    displayLabel = "#${tag.name}",
                    isSelected = tag.id == selectedListTagId,
                )
                ).distinctBy { it.id }.sortedBy { it.name.lowercase() }
    } else {
        listTagOptions
    },
    entryIsDirty = true,
    entryRevision = entryRevision + 1,
    tagOperation = OperationState.Idle,
    tagCreateOperation = OperationState.Idle,
).withEditorState()
