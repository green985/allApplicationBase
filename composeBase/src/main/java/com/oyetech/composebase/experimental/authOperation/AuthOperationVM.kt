package com.oyetech.composebase.experimental.authOperation

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.AgeChanged
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.GenderChanged
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.LoginClicked
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.OnCancelProfile
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.OnSubmitProfile
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.UsernameChanged
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.firebaseModels.userModel.isProfileCompletedForAuth
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthOperationVM(
    appDispatchers: AppDispatchers,
    private val authOperationRepository: AuthOperationRepository,
    val navigationUseCase: NavigationUseCase,
    private val googleLoginRepository: GoogleLoginRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    val authOperationState = MutableStateFlow(AuthOperationUiState())
    val uiEvent = MutableSharedFlow<AuthOperationUiEvent>()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            authOperationState.collectLatest {
                Timber.d("AuthOperationState updated: $it")
            }
        }
        // Restore login state from persisted user data on app start
        viewModelScope.launch(getDispatcherIo()) {
            authOperationRepository.userDataStateFlow.collectLatest { userData ->
                mapUserDataToState(userData)
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is AuthOperationEvent) {
            when (event) {
                LoginClicked -> handleLoginClicked()
                is UsernameChanged -> authOperationState.updateState {
                    copy(username = event.username, isUsernameEmpty = event.username.isBlank())
                }

                is AgeChanged -> authOperationState.updateState { copy(age = event.age) }
                is GenderChanged -> authOperationState.updateState { copy(gender = event.gender) }
                OnSubmitProfile -> handleSubmitProfile()
                OnCancelProfile -> handleCancelProfile()
                AuthOperationEvent.DeleteAccountClick -> handleDeleteAccount()
                AuthOperationEvent.ErrorDismiss -> authOperationState.updateState {
                    copy(isError = false, errorMessage = "")
                }
            }
        }
    }

    private fun handleLoginClicked() {
        authOperationState.updateState {
            copy(isLoading = true, isError = false, errorMessage = "")
        }

        viewModelScope.launch(getDispatcherIo()) {
            authOperationRepository.loginWithGoogleAndSyncUser().fold(
                onSuccess = { userData ->
                    mapUserDataToState(userData)
                    authOperationState.updateState { copy(isLoading = false) }
                    if (userData.isProfileCompletedForAuth()) {
                        uiEvent.emit(AuthOperationUiEvent.OnLoginSuccess)
                        navigationUseCase.navigateTo("back")
                    } else {
                        uiEvent.emit(AuthOperationUiEvent.OnProfileIncomplete)
                        navigationUseCase.navigateTo(QuestionAppProjectRoutes.CompleteProfileScreen.route)
                    }
                },
                onFailure = { error ->
                    authOperationState.updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = ErrorMessage.fetchErrorMessage(error.message)
                        )
                    }
                }
            )
        }
    }

    private fun handleSubmitProfile() {
        if (isErrorInProfileForm()) return

        authOperationState.updateState {
            copy(
                isLoading = true,
                isError = false,
                errorMessage = ""
            )
        }

        viewModelScope.launch(getDispatcherIo()) {
            val state = authOperationState.value
            authOperationRepository.updateUserProfile(
                username = state.username,
                age = state.age,
                gender = state.gender,
            ).fold(
                onSuccess = { userData ->
                    mapUserDataToState(userData)
                    authOperationState.updateState { copy(isLoading = false) }
                    uiEvent.emit(AuthOperationUiEvent.OnLoginSuccess)
                    navigationUseCase.navigateTo("back")
                },
                onFailure = { error ->
                    Timber.e("updateUserProfile error: ${error.message}")
                    authOperationState.updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = ErrorMessage.fetchErrorMessage(error.message)
                        )
                    }
                }
            )
        }
    }

    private fun handleCancelProfile() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.removeUser(googleLoginRepository.getUserUid())
            uiEvent.emit(AuthOperationUiEvent.OnProfileCancelled)
            navigationUseCase.navigateTo("back")
        }
    }

    private fun handleDeleteAccount() {
        authOperationState.updateState { copy(isLoading = true) }
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.removeUser(googleLoginRepository.getUserUid())
            delay(500)
            snackbarDelegate.triggerSnackbarState(LanguageKey.deleteAccountSuccess)
            authOperationState.value = AuthOperationUiState()
            uiEvent.emit(AuthOperationUiEvent.OnProfileCancelled)
            navigationUseCase.navigateTo("back")
        }
    }

    private fun isErrorInProfileForm(): Boolean {
        val state = authOperationState.value

        if (state.username.isBlank()) {
            authOperationState.updateState {
                copy(
                    isError = true,
                    isUsernameEmpty = true,
                    errorMessage = LanguageKey.usernameIsEmpty
                )
            }
            return true
        }

        if (state.age.isBlank()) {
            authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.ageCannotBeNull)
            }
            return true
        }

        try {
            val ageVal = state.age.toInt()
            if (ageVal < 18 || ageVal > 100) {
                authOperationState.updateState {
                    copy(isError = true, errorMessage = LanguageKey.invalidAgeError)
                }
                return true
            }
        } catch (e: Exception) {
            authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.ageCannotBeNull)
            }
            return true
        }

        if (state.gender.isBlank()) {
            authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.genderCannotBeEmpty)
            }
            return true
        }

        return false
    }
}
