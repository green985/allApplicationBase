package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.updateState
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import kotlinx.coroutines.Dispatchers
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
//
//    if (userData.isUserDeleted()) {
//        viewModelScope.launch(Dispatchers.Main) {
//            loginOperationState.updateState {
//                LoginOperationUiState(isUserDeleted = true)
//            }
//        }
//        return
//    }


    if (userData.errorException != null) {
        viewModelScope.launch(Dispatchers.Main) {
            loginOperationState.updateState {
                copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = userData.errorException?.message ?: LanguageKey.generalErrorText
                )
            }
        }
        return
    }

    // todo anoynmous user icin ayarlamalar burdan yapilacak
    if (userData.userId.isNotBlank()) {
        // google tarafindan token aldik lakin user daha kayitli degil...

        if (userData.isProfileComplete()) {
            Timber.d("LoginOperationVM mapToProfileValue user profile complete$userData")

            viewModelScope.launch(getDispatcherIo()) {
                uiEvent.emit(LoginOperationUiEvent.OnLoginSuccess)
            }

            viewModelScope.launch(Dispatchers.Main) {
                loginOperationState.updateState {
                    LoginOperationUiState(
                        displayNameRemote = userData.username,
                        uid = userData.userId,
                        isAnonymous = userData.isAnonymous,
                        lastSignInTimestamp = userData.lastSignInTimestamp,
                    )
                }
            }
        } else {
            Timber.d("LoginOperationVM mapToProfileValue fail$userData")
            viewModelScope.launch(Dispatchers.Main) {
                navigationUseCase.navigate(com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.CompleteProfileScreen.route)
                loginOperationState.updateState {
                    LoginOperationUiState(isRegistrationCompleteNeeded = true)
                }
            }
        }


    } else {
        val isLoading = loginOperationState.value.isLoading
        loginOperationState.value = LoginOperationUiState(
            isLoading = isLoading,
        )
    }
}
