package com.oyetech.composebase.baseViews.globalLoading.delegate

import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import kotlinx.coroutines.flow.StateFlow

/**
Created by Erdi Özbek
-13.10.2024-
-20:19-
 **/

interface GlobalLoadingDelegate {

    val globalLoadingUiState: StateFlow<GlobalLoadingUiState>

    fun setGlobalLoadingState(
        globalLoadingState: Boolean,
        errorMessage: String = "",
        retry: () -> Unit = {},
    )

    fun onRetryEvent()

}