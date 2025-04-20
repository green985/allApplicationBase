package com.oyetech.composebase.experimental.loginOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.loginOperationNew.mapToProfileValue
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.AgeChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.DeleteAccountClick
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.ErrorDismiss
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.GenderChanged
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.LoginClicked
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.OnCancel
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.OnSubmit
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.UsernameChanged
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.domain.repository.firebase.FirebaseTokenOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.googleAuth.isUserHasUID
import com.oyetech.tools.coroutineHelper.AppDispatchers
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
    appDispatchers: AppDispatchers,
    private val googleLoginRepository: GoogleLoginRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val firebaseTokenOperationRepository: FirebaseTokenOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    val loginOperationState =
        MutableStateFlow(LoginOperationUiState())

    val uiEvent = MutableSharedFlow<LoginOperationUiEvent>()

    init {
        Timber.d("LoginOperationVM init")
        if (GeneralSettings.isLoginOperationEnable()) {
            observeGoogleUserStateFlow()
            observeUserProfileState()
            googleLoginRepository.autoLoginOperation()
            updateUserToken()
        }
    }

    fun getLoginOperationSharedState(): SharedFlow<LoginOperationUiState> {
        return loginOperationState.asSharedFlow()
    }

    private fun updateUserToken() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseTokenOperationRepository.firebaseTokenStateFlow.collectLatest { firebaseTokenOperationModel ->
                if (firebaseTokenOperationModel?.notificationToken?.isBlank() == false) {
                    uiEvent.collectLatest {
                        if (it is LoginOperationUiEvent.OnLoginSuccess) {
                            Timber.d("LoginOperationVM updateUserToken")
                            firebaseUserRepository.updateUserNotificationToken(
                                firebaseTokenOperationModel.notificationToken
                            )
                        } else {
                            Timber.d("LoginOperationVM updateUserToken else")
                        }
                    }
                }
            }
        }
    }

    private fun observeUserProfileState() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserRepository.userDataStateFlow.asResult().onEach {
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

    private fun observeGoogleUserStateFlow() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.googleUserStateFlow.asResult().onEach {
                it.fold(
                    onSuccess = { googleUserResponseData ->
                        if (googleUserResponseData.isUserHasUID()) {
                            val firebaseProfileUserModel =
                                googleUserResponseData.toFirebaseUserProfileModel()
                            firebaseUserRepository.getUserProfileWithUid(firebaseProfileUserModel)
                        } else if (googleUserResponseData.errorException != null) {
                            loginOperationState.value = LoginOperationUiState(
                                isError = true,
                                errorMessage = googleUserResponseData.errorException?.message ?: ""
                            )
                        }
                    },
                    onFailure = {
                        loginOperationState.value = LoginOperationUiState(
                            isError = true,
                            errorMessage = it.message ?: ""
                        )
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
//                        googleLoginRepository.signInWithGoogle()
                        googleLoginRepository.signInWithGoogleAnonymous()
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
            firebaseUserRepository.deleteUser(googleLoginRepository.getUserUid())
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
            val userData = firebaseUserRepository.userDataStateFlow.value
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
            firebaseUserRepository.updateUserProperty(editedUserData)
        }
        return false
    }

    private fun isErrorInLoginForm(): Boolean {
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