package com.oyetech.kmpmodels.ui.state

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import kotlinx.datetime.LocalDate

data class DiaryUiState(
    val selectedDate: LocalDate,
    val dayOffset: Int = 0,
    val isTodaySelected: Boolean,
    val entries: List<EntryEntity>,
    val selectedDateLabel: String = "",
    val entryItems: List<DiaryEntryUiState> = emptyList(),
    val areaOptions: List<DiaryAreaUiState> = emptyList(),
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
    val timerStatus: String = "",
    val timerLabel: String = "",
    val primaryButtonLabel: String = "",
    val canCreateOrUpdateEntry: Boolean = false,
    val dismissRequested: Boolean = false,
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
)

data class DiaryAreaUiState(
    val area: AreaEntry,
    val label: String,
)
