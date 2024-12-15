package com.oyetech.composebase.baseViews.globalToolbar.delegate

import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiState
import kotlinx.coroutines.flow.StateFlow

/**
Created by Erdi Özbek
-13.10.2024-
-19:40-
 **/

interface GlobalToolbarDelegate {
    val globalToolbarUiState: StateFlow<GlobalToolbarUiState>
    fun setToolbarState(globalToolbarUiState: GlobalToolbarUiState)
    fun onActionButtonClick()

    fun setTitle(title: String)
    fun showBackButton(show: Boolean)
    fun setActionButton(buttonText: String, onClick: () -> Unit)
    fun clearActionButton()
}