package com.oyetech.composebase.baseViews.globalLoading.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiEvent
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Error
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Idle
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState.Loading

@Preview
@Composable
fun PartialLoadingScreen(
    uiState: GlobalLoadingUiState = Idle,
    onEvent: (GlobalLoadingUiEvent) -> Unit = {},
) {
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
