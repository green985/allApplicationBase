package com.oyetech.kmpmodels.ui.state

data class HomeUiState(
    val welcomeMessage: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val showGoogleLogin: Boolean = false,
    val canRetrySession: Boolean = false,
)
