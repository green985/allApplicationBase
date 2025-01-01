package com.oyetech.composebase.baseViews.snackbar

import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-1.01.2025-
-23:19-
 **/

class SnackbarDelegate {

    val snacbarUiState = MutableStateFlow(SnackbarUiState())

    fun triggerSnackbarState(
        message: String,
        actionLabel: String? = null,
    ) {
        snacbarUiState.value = SnackbarUiState(
            message = message,
            actionLabel = actionLabel,
        )
    }
}