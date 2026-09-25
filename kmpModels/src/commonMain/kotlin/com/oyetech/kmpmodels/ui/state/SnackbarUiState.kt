package com.oyetech.kmpmodels.ui.state

data class SnackbarUiState(
    val uuid: Long = 0L,
    val message: String = "",
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
)
