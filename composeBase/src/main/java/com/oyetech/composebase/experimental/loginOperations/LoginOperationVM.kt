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
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
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
    val googleLoginRepository: GoogleLoginRepository,
    val profileRepository: FirebaseUserRepository,
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
                        mapToSignInValue(googleUserResponseData)
                    },
                    onFailure = {
                        Timber.d(" Google User State Flow Error: $it")
                    }
                )
            }.collect()
        }
        viewModelScope.launch(getDispatcherIo()) {
            profileRepository.userDataStateFlow.asResult().onEach {
                delay(1000)
                Timber.d(" Google User State Flow: $it")
                it.fold(
                    onSuccess = { userData ->
                        mapToProfileValue(userData)
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
                loginOperationState.updateState {
                    copy(
                        isError = false,
                        errorMessage = ""
                    )
                }
            }

            is UsernameChanged -> {
                loginOperationState.updateState {
                    copy(
                        displayName = event.username,
                        isUsernameEmpty = event.username.isBlank()
                    )
                }
            }

            UsernameSetClicked -> {
                loginOperationState.updateState {
                    copy(isLoading = true)
                }
                viewModelScope.launch(getDispatcherIo()) {
                    profileRepository.updateUserName(loginOperationState.value.displayName)
                }
            }
        }
    }

}