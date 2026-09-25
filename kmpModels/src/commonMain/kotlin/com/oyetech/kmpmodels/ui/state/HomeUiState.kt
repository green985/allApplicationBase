package com.oyetech.kmpmodels.ui.state

data class HomeUiState(
    val welcomeMessage: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)
