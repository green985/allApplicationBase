package com.oyetech.composebase.experimental.loginOperationNew

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-17.04.2025-
-15:21-
 **/

fun LoginOperationVM.mapToProfileValue(userData: FirebaseUserProfileModel?) {
    if (userData == null) {
        return
    }

    if (userData.isUserDeleted) {
        loginOperationState.value =
            LoginOperationUiState(isUserDeleted = true)
        return
    }


    if (userData.errorException != null) {
        loginOperationState.updateState {
            copy(
                isLoading = false,
                isError = true,
                errorMessage = userData.errorException?.message ?: LanguageKey.generalErrorText
            )
        }
        return
    }

    // todo anoynmous user icin ayarlamalar burdan yapilacak
    if (userData.userId.isNotBlank()) {
        // google tarafindan token aldik lakin user daha kayitli degil...

        if (userData.isProfileComplete()) {
            Timber.d("LoginOperationVM mapToProfileValue user profile complete${userData.toString()}")

            viewModelScope.launch(getDispatcherIo()) {
                uiEvent.emit(LoginOperationUiEvent.OnLoginSuccess)
            }
            loginOperationState.value = LoginOperationUiState(
                displayNameRemote = userData.username ?: "",
                uid = userData.userId,
                isAnonymous = userData.isAnonymous,
                lastSignInTimestamp = userData.lastSignInTimestamp,
            )
        } else {
            Timber.d("LoginOperationVM mapToProfileValue fail${userData.toString()}")
            loginOperationState.value =
                LoginOperationUiState(isRegistrationCompleteNeeded = true)
        }


    } else {
        val isLoading = loginOperationState.value.isLoading
        loginOperationState.value = LoginOperationUiState(
            isLoading = isLoading,
        )
    }
}
