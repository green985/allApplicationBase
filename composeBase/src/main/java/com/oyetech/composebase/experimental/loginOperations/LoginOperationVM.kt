package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.ErrorDismiss
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.LoginClicked
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameSetClicked
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.isUserLogin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-22.12.2024-
-02:24-
 **/

class LoginOperationVM(
    appDispatchers: AppDispatchers,
    private val googleLoginRepository: GoogleLoginRepository,
) : BaseViewModel(appDispatchers) {

    val loginOperationState = MutableStateFlow(LoginOperationUiState())

    init {
        observeProfileData()
    }

    private fun observeProfileData() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.googleUserStateFlow.asResult().onEach {
                delay(1000)
                Timber.d(" Google User State Flow: $it")
                it.fold(
                    onSuccess = { googleUserResponseData ->
                        mapToProfileResult(googleUserResponseData)
                    },
                    onFailure = {
                        Timber.d(" Google User State Flow Error: $it")
                    }
                )
            }.collect()
        }
    }

    fun handleEvent(event: LoginOperationEvent) {
        when (event) {
            LoginClicked -> {
                loginOperationState.value = LoginOperationUiState(isLoading = true)
                googleLoginRepository.signInWithGoogleAnonymous()
            }

            ErrorDismiss -> {
                loginOperationState.value = LoginOperationUiState()
            }

            UsernameChanged -> {
            }

            UsernameSetClicked -> {

            }
        }
    }

    fun mapToProfileResult(googleUserResponseData: GoogleUserResponseData) {
        if (googleUserResponseData.isUserLogin()) {
            loginOperationState.updateState {
                LoginOperationUiState(
                    displayName = googleUserResponseData.displayName ?: "",
                    uid = googleUserResponseData.uid
                )
            }
        } else if (googleUserResponseData.errorMessage?.isNotBlank() == true) {
            loginOperationState.value = LoginOperationUiState(
                isError = true,
                errorMessage = googleUserResponseData.errorMessage ?: ""
            )
        }
    }

}