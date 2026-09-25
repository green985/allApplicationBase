package com.oyetech.kmpmodels.ui.state

import kotlinx.datetime.LocalDate

data class DailyPagerUiState(
    val selectedDate: LocalDate,
    val dayOffset: Int = 0,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)
