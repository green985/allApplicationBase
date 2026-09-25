package com.oyetech.kmpmodels.ui.state

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
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
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val quoteIsDirty: Boolean = false,
    val quoteIsSaving: Boolean = false,
    val quoteRevision: Long = 0L,
    val entryIsDirty: Boolean = false,
    val entryIsSaving: Boolean = false,
    val entryRevision: Long = 0L,
)

data class DiaryEntryUiState(
    val id: String,
    val areaLabel: String,
    val createdBy: String,
    val timeLabel: String,
    val text: String,
)

data class DiaryAreaUiState(
    val area: AreaEntry,
    val label: String,
)
