package com.oyetech.composebase.baseViews.globalToolbar.delegate

import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
Created by Erdi Özbek
-13.10.2024-
-19:39-
 **/

class GlobalToolbarDelegateImp : GlobalToolbarDelegate {
    private val _uiState = MutableStateFlow(GlobalToolbarUiState())  // Başlangıç durumu
    override val globalToolbarUiState: StateFlow<GlobalToolbarUiState> = _uiState

    override fun setToolbarState(globalToolbarUiState: GlobalToolbarUiState) {
        _uiState.value = globalToolbarUiState
    }

    override fun onActionButtonClick() {
        _uiState.value.onActionButtonClick?.invoke()
    }

    override fun setTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    override fun showBackButton(show: Boolean) {
        _uiState.value = _uiState.value.copy(showBackButton = show)
    }

    override fun setActionButton(buttonText: String, onClick: () -> Unit) {
        _uiState.value =
            _uiState.value.copy(actionButtonText = buttonText, onActionButtonClick = onClick)
    }

    override fun clearActionButton() {
        _uiState.value = _uiState.value.copy(actionButtonText = null, onActionButtonClick = null)
    }

}
