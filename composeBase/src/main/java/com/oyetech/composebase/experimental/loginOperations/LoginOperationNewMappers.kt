package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.updateState
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

    // Profile complete kontrolü
    if (userData.username.isNotBlank() && userData.age.isNotBlank() && userData.gender.isNotBlank()) {
        Timber.d("LoginOperationVM mapToProfileValue user profile complete: $userData")
        viewModelScope.launch(Dispatchers.Main) {
            loginOperationState.updateState {
                LoginOperationUiState(
                    displayNameRemote = userData.username,
                    uid = userData.userId,
                    isAnonymous = userData.isAnonymous,
                    lastSignInTimestamp = userData.lastSignInTimestampTmp,
                )
            }
        }
        viewModelScope.launch(getDispatcherIo()) {
            uiEvent.emit(LoginOperationUiEvent.OnLoginSuccess)
            navigationUseCase.navigateTo("back")
        }
    } else {
        Timber.d("LoginOperationVM mapToProfileValue profile incomplete: $userData")
        viewModelScope.launch(Dispatchers.Main) {
            navigationUseCase.navigateTo(com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.CompleteProfileScreen.route)
            loginOperationState.updateState {
                LoginOperationUiState(isRegistrationCompleteNeeded = true)
            }
        }
    }
}
