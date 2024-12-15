package com.oyetech.composebase.baseViews.globalLoading.delegate

import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
Created by Erdi Özbek
-13.10.2024-
-20:19-
 **/

class GlobalLoadingDelegateImp : GlobalLoadingDelegate {

    private val _uiState = MutableStateFlow<GlobalLoadingUiState>(GlobalLoadingUiState.Idle)
    override val globalLoadingUiState: StateFlow<GlobalLoadingUiState>
        get() = _uiState

    override fun setGlobalLoadingState(
        globalLoadingState: Boolean,
        errorMessage: String,
        retry: () -> Unit,
    ) {
        _uiState.value = if (globalLoadingState) {
            GlobalLoadingUiState.Loading
        } else {
            if (errorMessage.isNotEmpty()) {
                GlobalLoadingUiState.Error(errorMessage, retry)
            } else {
                GlobalLoadingUiState.Idle
            }
        }
    }

    override fun onRetryEvent() {
        (_uiState.value as? GlobalLoadingUiState.Error)?.errorAction
    }
}