package com.oyetech.composebase.baseViews.snackbar

/**
Created by Erdi Özbek
-1.01.2025-
-23:20-
 **/

data class SnackbarUiState(
    val uuid: Long = System.currentTimeMillis(),
    val message: String = "",
    val actionLabel: String? = null,
)