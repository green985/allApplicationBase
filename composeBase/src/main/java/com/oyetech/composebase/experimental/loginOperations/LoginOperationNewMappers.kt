package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-17.04.2025-
-15:21-
 **/

fun LoginOperationVM.mapToProfileValue(userData: UserProfileProperty?) {
    if (userData == null) {
        return
    }

    if (userData.userId.isBlank()) {
        val isLoading = loginOperationState.value.isLoading
        loginOperationState.value = LoginOperationUiState(
            isLoading = isLoading,
            errorMessage = LanguageKey.userIdNotFound
        )
        return
    }

    if (userData.isProfileCompletedForAuth()) {
        Timber.d("LoginOperationVM mapToProfileValue user profile complete: $userData")
        viewModelScope.launch(Dispatchers.Main) {
            loginOperationState.updateState {
                LoginOperationUiState(username = userData.username)
            }
        }
        viewModelScope.launch(getDispatcherIo()) {
            uiEvent.emit(LoginOperationUiEvent.OnLoginSuccess)
            navigationUseCase.goBack()
        }
    } else {
        Timber.d("LoginOperationVM mapToProfileValue profile incomplete: $userData")
        viewModelScope.launch(Dispatchers.Main) {
            navigationUseCase.navigateTo(AppRoute.CompleteProfileScreen)
            loginOperationState.updateState {
                LoginOperationUiState(isRegistrationCompleteNeeded = true)
            }
        }
    }
}
