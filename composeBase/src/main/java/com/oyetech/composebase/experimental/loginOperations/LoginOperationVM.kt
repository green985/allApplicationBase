package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.AgeChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.DeleteAccountClick
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.ErrorDismiss
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.GenderChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.LoginClicked
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.OnCancel
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.OnSubmit
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameChanged
import com.oyetech.composebase.experimental.viewModelSlice.UserOperationViewModelSlice
import com.oyetech.domain.repository.firebase.FirebaseTokenOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
    val firebaseTokenOperationRepository: FirebaseTokenOperationRepository,
    val userOperationViewModelSlice: UserOperationViewModelSlice,
    val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    val loginOperationState =
        MutableStateFlow(LoginOperationUiState())

    val uiEvent = MutableSharedFlow<LoginOperationUiEvent>()

    init {
        Timber.d("LoginOperationVM init")
        observeUserNotificationToken()
        googleLoginRepository.autoLoginOperation()
        observeProfileData()
        observeUserState()
    }

    private fun observeUserNotificationToken() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseTokenOperationRepository.firebaseTokenStateFlow.asResult().collectLatest {
                Timber.d("Firebase Token State Flow: $it")
            }
        }
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
                viewModelScope.launch(getDispatcherIo()) {
                    try {
                        googleLoginRepository.signInWithGoogle()
//                        googleLoginRepository.signInWithGoogleAnonymous()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
                deleteUserOperation()
            }

            is UsernameChanged -> {
                loginOperationState.updateState {
                    copy(
                        displayName = event.username,
                        isUsernameEmpty = event.username.isBlank()
                    )
                }
            }

            OnSubmit -> {
                if (onSubmitOperation()) return
            }

            OnCancel -> {
                if (deleteUserOperation()) return
            }

            is AgeChanged -> {
                loginOperationState.updateState {
                    copy(
                        age = event.age.toString()
                    )
                }
            }

            is GenderChanged -> {
                loginOperationState.updateState {
                    copy(gender = event.gender)
                }
            }
        }
    }

    private fun deleteUserOperation(): Boolean {
        viewModelScope.launch(getDispatcherIo()) {
            profileRepository.deleteUser(googleLoginRepository.getUserUid())
            googleLoginRepository.removeUser(googleLoginRepository.getUserUid())
            delay(500)
            snackbarDelegate.triggerSnackbarState(LanguageKey.deleteAccountSuccess)
            loginOperationState.value = LoginOperationUiState()
            uiEvent.emit(LoginOperationUiEvent.OnCancelUserCreation)
        }
        return true
    }

    private fun onSubmitOperation(): Boolean {
        if (isErrorInLoginForm()) {
            return true
        }

        loginOperationState.updateState {
            copy(isLoading = true)
        }
        viewModelScope.launch(getDispatcherIo()) {
            val userData = profileRepository.userDataStateFlow.value
            if (userData == null) {
                loginOperationState.updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "User not found"
                    )
                }
                return@launch
            }

            val editedUserData = userData.copy(
                username = loginOperationState.value.displayName,
                age = loginOperationState.value.age,
                gender = loginOperationState.value.gender
            )
            profileRepository.updateUserProperty(editedUserData)
        }
        return false
    }

    fun isErrorInLoginForm(): Boolean {
        if (loginOperationState.value.displayName.isBlank()) {
            loginOperationState.updateState {
                copy(
                    isError = true,
                    errorMessage = LanguageKey.usernameIsEmpty
                )
            }
            return true
        }
        if (loginOperationState.value.age.isBlank()) {
            loginOperationState.updateState {
                copy(
                    isError = true,
                    errorMessage = LanguageKey.ageCannotBeNull
                )
            }
            return true
        }
        try {
            val ageInvalid =
                loginOperationState.value.age.toInt() < 18 || loginOperationState.value.age.toInt() > 100
            loginOperationState.updateState {
                copy(
                    isError = true,
                    errorMessage = LanguageKey.invalidAgeError
                )
            }
            if (ageInvalid) {
                return true
            }
        } catch (e: Exception) {
            loginOperationState.updateState {
                copy(
                    isError = true,
                    errorMessage = LanguageKey.ageCannotBeNull
                )
            }
            return true
        }

        if (loginOperationState.value.gender.isBlank()) {
            loginOperationState.updateState {
                copy(
                    isError = true,
                    errorMessage = LanguageKey.genderCannotBeEmpty
                )
            }
            return true
        }
        return false
    }


}