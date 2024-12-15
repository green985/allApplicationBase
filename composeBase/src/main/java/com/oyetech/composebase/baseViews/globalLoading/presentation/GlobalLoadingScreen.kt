package com.oyetech.composebase.baseViews.globalLoading.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiEvent
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Error
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Idle
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Loading
import com.oyetech.composebase.helpers.viewProperties.fullScreenDialogProperties

/**
Created by Erdi Özbek
-13.10.2024-
-22:36-
 **/

@Composable
fun GlobalLoadingScreen(
    uiState: GlobalLoadingUiState = Idle,
    onEvent: (GlobalLoadingUiEvent) -> Unit = {},
) {

    val isDialogVisible = uiState != Idle

    if (isDialogVisible) {
        Dialog(onDismissRequest = {}, properties = fullScreenDialogProperties) {
            when (uiState) {
                is Idle -> {
                    // Idle durumda herhangi bir şey gösterilmiyor
                    Unit
                }

                is Loading -> {
                    GlobalLoadingView()
                }

                is Error -> {
                    GlobalErrorView(uiState = uiState, onEvent = onEvent)
                }
            }
        }
    }
}
