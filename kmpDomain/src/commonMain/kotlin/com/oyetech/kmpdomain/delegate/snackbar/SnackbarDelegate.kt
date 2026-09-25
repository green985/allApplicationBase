package com.oyetech.kmpdomain.delegate.snackbar

import com.oyetech.kmpmodels.ui.state.SnackbarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SnackbarDelegate {
    private val mutableSnackbarUiState = MutableStateFlow(SnackbarUiState())
    private var nextEventId = 0L
    val snackbarUiState: StateFlow<SnackbarUiState> = mutableSnackbarUiState.asStateFlow()

    fun triggerSnackbarState(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
    ) {
        mutableSnackbarUiState.value = SnackbarUiState(
            uuid = ++nextEventId,
            message = message,
            actionLabel = actionLabel,
            onAction = onAction,
        )
    }
}
