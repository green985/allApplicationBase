package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.DeleteAccountClick
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.ErrorDismiss
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.LoginClicked
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameSetClicked
import com.oyetech.composebase.experimental.viewModelSlice.UserOperationViewModelSlice
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    val googleLoginRepository: GoogleLoginRepository,
    val profileRepository: FirebaseUserRepository,
    val userOperationViewModelSlice: UserOperationViewModelSlice,
) : BaseViewModel(appDispatchers) {

    val loginOperationState =
        MutableStateFlow(LoginOperationUiState())

    init {
        Timber.d("LoginOperationVM init")
        googleLoginRepository.autoLoginOperation()
        observeProfileData()
        observeUserState()
    }

    fun getLoginOperationSharedState(): SharedFlow<LoginOperationUiState> {
        return loginOperationState.asSharedFlow()
    }

    private fun observeProfileData() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.googleUserStateFlow.asResult().onEach {
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
    }

    private fun observeUserState() {
        viewModelScope.launch(getDispatcherIo()) {
            profileRepository.userDataStateFlow.asResult().onEach {
                Timber.d(" profileRepository User State Flow: $it")
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

            DeleteAccountClick -> {
                loginOperationState.updateState {
                    copy(isLoading = true)
                }
                viewModelScope.launch(getDispatcherIo()) {
                    userOperationViewModelSlice.deleteUser(loginOperationState.value.uid)
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