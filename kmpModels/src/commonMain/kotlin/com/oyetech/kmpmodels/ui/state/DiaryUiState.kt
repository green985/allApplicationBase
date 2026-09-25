package com.oyetech.kmpmodels.ui.state

import com.oyetech.kmpmodels.entity.Area
import com.oyetech.kmpmodels.entity.EntryEntity
import kotlinx.datetime.LocalDate

data class DiaryUiState(
    val selectedDate: LocalDate,
    val isTodaySelected: Boolean,
    val entries: List<EntryEntity>,
    val isEditorVisible: Boolean,
    val selectedArea: Area?,
    val text: String,
    val dayQuote: String,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)
