package com.oyetech.composebase.experimental.loginOperations

import com.oyetech.composebase.base.updateState
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.isUserLogin
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel

/**
Created by Erdi Özbek
-24.12.2024-
-13:05-
 **/

fun LoginOperationVM.mapToSignInValue(googleUserResponseData: GoogleUserResponseData) {
    if (googleUserResponseData.isUserLogin()) {
        val firebaseProfileUserModel = googleUserResponseData.toFirebaseUserProfileModel()
        profileRepository.getUserProfileWithUid(firebaseProfileUserModel)
    } else if (googleUserResponseData.errorException != null) {
        loginOperationState.value = LoginOperationUiState(
            isError = true,
            errorMessage = googleUserResponseData.errorException?.message ?: ""
        )
    }
}

fun LoginOperationVM.mapToProfileValue(userData: FirebaseUserProfileModel?) {
    if (userData == null) {
        return
    }

    if (userData.isUserDeleted) {
        loginOperationState.value =
            LoginOperationUiState(isUserDeleted = userData.isUserDeleted)
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
        loginOperationState.value = LoginOperationUiState(
            displayNameRemote = userData.username ?: "",
            uid = userData.userId,
            isAnonymous = userData.isAnonymous,
            lastSignInTimestamp = userData.lastSignInTimestamp,
        )
    } else {
        val isLoading = loginOperationState.value.isLoading
        loginOperationState.value = LoginOperationUiState(
            isLoading = isLoading,
        )
    }


}

