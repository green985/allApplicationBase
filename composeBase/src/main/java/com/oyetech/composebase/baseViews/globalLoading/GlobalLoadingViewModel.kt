package com.oyetech.composebase.baseViews.globalLoading

import androidx.lifecycle.ViewModel
import com.oyetech.composebase.baseViews.globalLoading.delegate.GlobalLoadingDelegate
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiEvent

class GlobalLoadingViewModel(
    private val globalLoadingDelegate: GlobalLoadingDelegate,
) : ViewModel(), GlobalLoadingDelegate by globalLoadingDelegate {

    val uiState = globalLoadingUiState

    fun onEvent(event: GlobalLoadingUiEvent) {
        when (event) {
            is GlobalLoadingUiEvent.Retry -> {
                onRetryEvent()  // Tekrar deneme durumunu tetikle
            }

            is GlobalLoadingUiEvent.Dismiss -> {
                setGlobalLoadingState(false)
            }
        }
    }

}
