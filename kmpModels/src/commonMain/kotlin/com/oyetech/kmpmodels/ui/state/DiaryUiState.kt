package com.oyetech.kmpmodels.ui.state

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.entity.TagEntry
import kotlinx.datetime.LocalDate

data class DiaryUiState(
    val selectedDate: LocalDate,
    val dayOffset: Int = 0,
    val isTodaySelected: Boolean,
    val entries: List<EntryEntity>,
    val selectedDateLabel: String = "",
    val entryItems: List<DiaryEntryUiState> = emptyList(),
    val areaOptions: List<DiaryAreaUiState> = emptyList(),
    val areaOptionRows: List<List<DiaryAreaUiState>> = emptyList(),
    val isAreaMenuVisible: Boolean = false,
    val selectedListArea: AreaEntry? = null,
    val areaFilterLabel: String = "",
    val isDailyContentVisible: Boolean = true,
    val isAreaEntriesEmpty: Boolean = false,
    val timerPresetOptions: List<DiaryTimerPresetUiState> = emptyList(),
    val isEditorVisible: Boolean,
    val editingEntryId: String? = null,
    val selectedArea: AreaEntry?,
    val text: String,
    val dayQuote: String,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
    val quoteOperation: OperationState = OperationState.Idle,
    val entryOperation: OperationState = OperationState.Idle,
    val entriesOperation: OperationState = OperationState.Idle,
    val quoteIsDirty: Boolean = false,
    val quoteRevision: Long = 0L,
    val entryIsDirty: Boolean = false,
    val entryRevision: Long = 0L,
    val selectedTimerPreset: EntryTimerPreset? = null,
    val customDurationMinutes: String = "",
    val selectedDurationSeconds: Long? = null,
    val isEntryCompleted: Boolean = false,
    val timerStatus: EntryTimerStatus = EntryTimerStatus.NONE,
    val timerLabel: String = "",
    val primaryButtonLabel: String = "",
    val canCreateOrUpdateEntry: Boolean = false,
    val dismissRequested: Boolean = false,
    val isCustomDurationVisible: Boolean = false,
    val customDurationError: String = "",
    val hasCustomDurationError: Boolean = false,
    val canChangeDuration: Boolean = true,
    val canCancelEditorTimer: Boolean = false,
    val isHtmlExportAvailable: Boolean = false,
    val canExportArchive: Boolean = true,
    val hasEntryTextError: Boolean = false,
    val entryTextError: String = "",
    val selectedTag: TagEntry? = null,
    val selectedTagLabel: String = "",
    val tagQuery: String = "",
    val tagSuggestions: List<DiaryTagUiState> = emptyList(),
    val isTagInputVisible: Boolean = false,
    val canCreateTag: Boolean = false,
    val createTagLabel: String = "",
    val canClearTag: Boolean = false,
    val tagQueryError: String = "",
    val hasTagQueryError: Boolean = false,
    val tagOperation: OperationState = OperationState.Idle,
    val tagCreateOperation: OperationState = OperationState.Idle,
)

data class DiaryEntryUiState(
    val id: String,
    val areaLabel: String,
    val createdBy: String,
    val timeLabel: String,
    val text: String,
    val timerStatusLabel: String = "",
    val countdownLabel: String? = null,
    val isTimerRunning: Boolean = false,
    val canStartTimer: Boolean = false,
    val canCancelTimer: Boolean = false,
    val canMarkCompleted: Boolean = false,
    val isAreaVisible: Boolean = false,
    val isTimerStatusVisible: Boolean = false,
    val tagLabel: String? = null,
    val areaEmoji: String = "",
    val dateLabel: String? = null,
)

data class DiaryTagUiState(
    val id: String,
    val name: String,
    val displayLabel: String,
    val isSelected: Boolean,
)

data class DiaryAreaUiState(
    val area: AreaEntry,
    val label: String,
    val emoji: String,
    val description: String,
)

data class DiaryTimerPresetUiState(
    val preset: EntryTimerPreset,
    val label: String,
    val isEnabled: Boolean = true,
)
