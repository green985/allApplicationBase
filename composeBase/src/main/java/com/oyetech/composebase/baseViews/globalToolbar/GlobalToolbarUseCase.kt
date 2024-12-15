package com.oyetech.composebase.baseViews.globalToolbar

import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GlobalToolbarUseCase {
    private val _uiState = MutableStateFlow(GlobalToolbarUiState())  // Başlangıç durumu
    val uiState: StateFlow<GlobalToolbarUiState> = _uiState

    fun setTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun showBackButton(show: Boolean) {
        _uiState.value = _uiState.value.copy(showBackButton = show)
    }

    fun setActionButton(buttonText: String, onClick: () -> Unit) {
        _uiState.value = _uiState.value.copy(
            actionButtonText = buttonText,
            onActionButtonClick = onClick
        )
    }

    fun clearActionButton() {
        _uiState.value = _uiState.value.copy(
            actionButtonText = null,
            onActionButtonClick = null
        )
    }
}
