package com.oyetech.kmpmodels.ui.state

data class LoginUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isAuthenticated: Boolean = false,
    val username: String = "",
    val errorMessage: String = "",
)
