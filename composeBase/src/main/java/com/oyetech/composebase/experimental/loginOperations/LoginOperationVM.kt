package com.oyetech.composebase.experimental.loginOperations

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-22.12.2024-
-02:24-
 **/

class LoginOperationVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(LoginOperationUiState())


}