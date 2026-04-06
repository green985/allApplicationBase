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
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthOperationVM(
    appDispatchers: AppDispatchers,
    private val authOperationRepository: AuthOperationRepository,
    val navigationUseCase: NavigationUseCase,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    private val _authOperationState = MutableStateFlow(AuthOperationUiState())
    val authOperationState = _authOperationState

    val uiEvent = MutableSharedFlow<AuthOperationUiEvent>()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            _authOperationState.collectLatest {
                Timber.d("_authOperationState updated: $it")
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
                is UsernameChanged -> _authOperationState.updateState {
                    copy(username = event.username, isUsernameEmpty = event.username.isBlank())
                }

                is AgeChanged -> _authOperationState.updateState { copy(age = event.age) }
                is GenderChanged -> _authOperationState.updateState { copy(gender = event.gender) }
                OnSubmitProfile -> handleSubmitProfile()
                OnCancelProfile -> handleCancelProfile()
                AuthOperationEvent.DeleteAccountClick -> handleDeleteAccount()
                AuthOperationEvent.ErrorDismiss -> _authOperationState.updateState {
                    copy(isError = false, errorMessage = "")
                }
            }
        }
    }

    private fun handleLoginClicked() {
        _authOperationState.updateState {
            copy(isLoading = true, isError = false, errorMessage = "")
        }

        viewModelScope.launch(getDispatcherIo()) {
            authOperationRepository.loginWithGoogleAndSyncUser().fold(
                onSuccess = { userData ->
                    mapUserDataToState(userData)
                    _authOperationState.updateState { copy(isLoading = false) }
                    if (userData.isProfileCompletedForAuth()) {
                        uiEvent.emit(AuthOperationUiEvent.OnLoginSuccess)
                        navigationUseCase.goBack()
                    } else {
                        uiEvent.emit(AuthOperationUiEvent.OnProfileIncomplete)
                        navigationUseCase.navigateTo(AppRoute.CompleteProfileScreen)
                    }
                },
                onFailure = { error ->
                    _authOperationState.updateState {
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

        _authOperationState.updateState {
            copy(
                isLoading = true,
                isError = false,
                errorMessage = ""
            )
        }

        viewModelScope.launch(getDispatcherIo()) {
            val state = _authOperationState.value
            authOperationRepository.updateUserProfile(
                username = state.username,
                age = state.age,
                gender = state.gender,
            ).fold(
                onSuccess = { userData ->
                    mapUserDataToState(userData)
                    _authOperationState.updateState { copy(isLoading = false) }
                    uiEvent.emit(AuthOperationUiEvent.OnLoginSuccess)
                    navigationUseCase.goBack()
                },
                onFailure = { error ->
                    Timber.e("updateUserProfile error: ${error.message}")
                    _authOperationState.updateState {
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
            uiEvent.emit(AuthOperationUiEvent.OnProfileCancelled)
            navigationUseCase.goBack()
        }
    }

    private fun handleDeleteAccount() {
        _authOperationState.updateState {
            copy(
                isLoading = true,
                isError = false,
                errorMessage = ""
            )
        }
        viewModelScope.launch(getDispatcherIo()) {
            authOperationRepository.deleteAccount().fold(
                onSuccess = {
                    _authOperationState.value = AuthOperationUiState()
                    snackbarDelegate.triggerSnackbarState(LanguageKey.deleteAccountSuccess)
                    uiEvent.emit(AuthOperationUiEvent.OnProfileCancelled)
                    navigationUseCase.goBack()
                },
                onFailure = { error ->
                    _authOperationState.updateState {
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

    fun getUserId(): String {
        return authOperationRepository.userDataStateFlow.value?.userId ?: ""
    }

    @Suppress("ReturnCount")
    private fun isErrorInProfileForm(): Boolean {
        val state = _authOperationState.value

        if (state.username.isBlank()) {
            _authOperationState.updateState {
                copy(
                    isError = true,
                    isUsernameEmpty = true,
                    errorMessage = LanguageKey.usernameIsEmpty
                )
            }
            return true
        }

        if (state.age.isBlank()) {
            _authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.ageCannotBeNull)
            }
            return true
        }

        try {
            val ageVal = state.age.toInt()
            if (ageVal < 18 || ageVal > 100) {
                _authOperationState.updateState {
                    copy(isError = true, errorMessage = LanguageKey.invalidAgeError)
                }
                return true
            }
        } catch (e: Exception) {
            _authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.ageCannotBeNull)
            }
            return true
        }

        if (state.gender.isBlank()) {
            _authOperationState.updateState {
                copy(isError = true, errorMessage = LanguageKey.genderCannotBeEmpty)
            }
            return true
        }

        return false
    }

    fun getToken(): String {
        return authOperationRepository.userDataStateFlow.value?.token ?: ""
    }
}
