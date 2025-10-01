package com.oyetech.composebase.sharedScreens.userProfile

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.userProfile.UserProfileEvent.OnEditProfile
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-3.05.2025-
-21:14-
 **/

class UserProfileVm(
    appDispatchers: AppDispatchers,
    private val username: String,
    private val navigationUseCase: NavigationUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
) :
    BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(UserProfileUiState(username = username))

    init {
    }

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            OnEditProfile -> {
                // Edit profile navigation not available after pruning quote feature
            }

        }
    }

    fun getUserProperty() {
        viewModelScope.launch(getDispatcherIo()) {

        }
    }
}