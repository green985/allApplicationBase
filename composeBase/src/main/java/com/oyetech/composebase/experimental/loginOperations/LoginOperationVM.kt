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
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseNotificationTokenOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.googleAuth.isUserHasUID
import com.oyetech.models.firebaseModels.googleAuth.toGoogleUserPostData
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
    val navigationUseCase: NavigationUseCase,
    val firebaseUserRepository: FirebaseUserRepository,
    private val firebaseNotificationTokenOperationRepository: FirebaseNotificationTokenOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val questionSupabaseRepository: com.oyetech.domain.repository.question.QuestionSupabaseRepository,
    private val sharedOperationRepository: SharedOperationRepository,
) : BaseViewModel(appDispatchers) {

    val loginOperationState =
        MutableStateFlow(LoginOperationUiState())

    val uiEvent = MutableSharedFlow<LoginOperationUiEvent>()

    init {
        Timber.d("LoginOperationVM init")
        if (GeneralSettings.isLoginOperationEnable()) {
            observeGoogleUserStateFlow()
            observeGoogleUserDataStateFlow()
            googleLoginRepository.autoLoginOperation2()
            updateUserNotificationToken()
        }
    }

    fun getLoginOperationSharedState(): SharedFlow<LoginOperationUiState> {
        return loginOperationState.asSharedFlow()
    }

    private fun updateUserNotificationToken() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseNotificationTokenOperationRepository.firebaseNotificationTokenStateFlow
                .collectLatest { firebaseTokenOperationModel ->
                    if (firebaseTokenOperationModel?.notificationToken?.isBlank() == false) {
                        uiEvent.collectLatest {
                            if (it is LoginOperationUiEvent.OnLoginSuccess) {
                                Timber.d(
                                    "LoginOperationVM updateUserToken + " +
                                            "${firebaseTokenOperationModel.notificationToken}"
                                )
//                            firebaseUserRepository.updateUserNotificationToken(
//                                firebaseTokenOperationModel.notificationToken
//                            )
                            } else {
                                Timber.d("LoginOperationVM updateUserToken else")
                            }
                        }
                    }
                }
        }
    }


    private fun observeGoogleUserStateFlow() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.googleUserStateFlow.asResult().onEach {
                it.fold(
                    onSuccess = { googleUserResponseData ->
                        if (googleUserResponseData.isUserHasUID()) {
                            questionSupabaseRepository.registerGoogleUser(
                                googleUserResponseData.toGoogleUserPostData()
                            ).asResult().collectLatest { result ->
                                Timber.d(" registerGoogleUser response: $result")
                                result.getOrNull()?.let { userProfileProperty ->
                                    sharedOperationRepository.saveGoogleUserData(userProfileProperty)
                                    mapToProfileValue2(userProfileProperty)
                                }
                            }
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

    private fun observeGoogleUserDataStateFlow() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.googleUserDataStateFlow.asResult().collectLatest {
                it.fold(
                    onSuccess = { userProfileProperty ->
                        if (userProfileProperty != null) {
                            Timber.d("Google user data found in shared prefs, calling getUserWithToken")
                            questionSupabaseRepository.getUserWithToken(
                                com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody(
                                    token = userProfileProperty.token
                                )
                            ).asResult().collectLatest { userResult ->
                                Timber.d(" getUserWithToken response: $userResult")
                                mapToProfileValue2(userResult.getOrNull())
                            }
                        }
                    },
                    onFailure = {
                        Timber.d(" Google User Data State Flow Error: $it")
                    }
                )
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is LoginOperationEvent) {
            when (event) {
                LoginClicked -> {
                    loginOperationState.updateState {
                        LoginOperationUiState(isLoading = true)
                    }
                    viewModelScope.launch(getDispatcherIo()) {
                        try {
                            googleLoginRepository.signInWithGoogle()
//                            googleLoginRepository.signInWithGoogleAnonymous()
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
                    viewModelScope.launch(getDispatcherIo()) {
                        googleLoginRepository.removeUser(googleLoginRepository.getUserUid())
                        navigationUseCase.navigateTo("back")
                        uiEvent.emit(LoginOperationUiEvent.OnCancelUserCreation)
                    }
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
    }

    private fun deleteUserOperation(): Boolean {
        viewModelScope.launch(getDispatcherIo()) {
//            firebaseUserRepository.deleteUser(googleLoginRepository.getUserUid())
            googleLoginRepository.removeUser(googleLoginRepository.getUserUid())
            delay(500)
            snackbarDelegate.triggerSnackbarState(LanguageKey.deleteAccountSuccess)
            loginOperationState.value = LoginOperationUiState()
            uiEvent.emit(LoginOperationUiEvent.OnCancelUserCreation)
            navigationUseCase.navigateTo("back")
        }
        return true
    }

    private fun onSubmitOperation(): Boolean {
        if (isErrorInLoginForm()) {
            Timber.d("onSubmitOperation: isErrorInLoginForm true")
            Timber.d("onSubmitOperation: isErrorInLoginForm ${loginOperationState.value.errorMessage}")
            return true
        }

        loginOperationState.updateState {
            copy(isLoading = true)
        }
        viewModelScope.launch(getDispatcherIo()) {
            val userData = googleLoginRepository.googleUserDataStateFlow.value
            if (userData == null) {
                Timber.d("onSubmitOperation: userData null")
                loginOperationState.updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = LanguageKey.generalErrorText
                    )
                }
                return@launch
            }

            val userProfileProperty =
                com.oyetech.models.firebaseModels.userModel.UserProfileProperty(
                    token = userData.token,
                    userId = userData.userId,
                    username = loginOperationState.value.displayName,
                    age = loginOperationState.value.age,
                    gender = loginOperationState.value.gender,
                    biography = userData.biography,
                    isAnonymous = userData.isAnonymous,
                    notificationToken = userData.notificationToken,
//                    lastSignInTimestampTmp = userData.lastSignInTimestampTmp,
//                    creationTimestamp = userData.creationTimestamp
                )

            questionSupabaseRepository.updateUser(userProfileProperty).asResult().collectLatest {
                Timber.d("updateUser response: $it")
                it.fold(
                    onSuccess = { updatedUser ->
                        uiEvent.emit(LoginOperationUiEvent.OnLoginSuccess)
                        firebaseUserRepository.updateUserProfileProperty(updatedUser)
                        navigationUseCase.navigateTo("back")
                    },
                    onFailure = { error ->
                        Timber.e("updateUser error: ${error.message}")
                        loginOperationState.updateState {
                            copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = error.message ?: "Update failed"
                            )
                        }
                    }
                )
            }
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