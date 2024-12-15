package com.oyetech.composebase.baseViews.globalLoading.usecase

import com.oyetech.composebase.baseViews.globalLoading.delegate.GlobalLoadingDelegate

/**
Created by Erdi Özbek
-13.10.2024-
-20:13-
 **/

class SetGlobalLoadingStateUseCase(private val globalLoadingDelegate: GlobalLoadingDelegate) {

    operator fun invoke(
        globalLoadingState: Boolean,
        errorMessage: String = "",
        retry: () -> Unit = {},
    ) {
        globalLoadingDelegate.setGlobalLoadingState(globalLoadingState, errorMessage, retry)
    }
}