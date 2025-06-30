package com.oyetech.composebase.sharedScreens.userProfile.editProfile;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileUiState
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-21.04.2025-
-15:19-
 **/

class EditProfileVm(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(EditProfileUiState())

    init {
    }

    override fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }
}