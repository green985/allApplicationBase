package com.oyetech.kmpmodels.ui.state

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import kotlinx.datetime.LocalDate

data class DiaryUiState(
    val selectedDate: LocalDate,
    val dayOffset: Int = 0,
    val isTodaySelected: Boolean,
    val entries: List<EntryEntity>,
    val isEditorVisible: Boolean,
    val selectedArea: AreaEntry?,
    val text: String,
    val dayQuote: String,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)
