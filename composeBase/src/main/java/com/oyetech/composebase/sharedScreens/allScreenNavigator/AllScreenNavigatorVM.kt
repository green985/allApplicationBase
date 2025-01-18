package com.oyetech.composebase.sharedScreens.allScreenNavigator;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-18.01.2025-
-11:07-
 **/

class AllScreenNavigatorVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(AllScreenNavigatorUiState())

    init {
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }
}