package com.oyetech.composebase.experimental.authOperation

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent.LoginClicked
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthOperationVM(
    appDispatchers: AppDispatchers,
    private val authOperationRepository: AuthOperationRepository,
) : BaseViewModel(appDispatchers) {

    val authOperationState = MutableStateFlow(AuthOperationUiState())
    val uiEvent = MutableSharedFlow<AuthOperationUiEvent>()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            authOperationState.collectLatest {
                Timber.d("AuthOperationState updated: $it")
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is AuthOperationEvent) {
            when (event) {
                LoginClicked -> handleLoginClicked()
            }
        }
    }

    private fun handleLoginClicked() {
        authOperationState.updateState {
            copy(
                isLoading = true,
                isError = false,
                errorMessage = ""
            )
        }

        viewModelScope.launch(getDispatcherIo()) {
            authOperationRepository.loginWithGoogleAndSyncUser().fold(
                onSuccess = { userData ->

                    
                    authOperationState.updateState {
                        copy(
                            isLoading = false,
                            userDataProperty = userData
                        )
                    }
                    uiEvent.emit(AuthOperationUiEvent.OnLoginSuccess)
                },
                onFailure = { error ->
                    authOperationState.updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = error.message ?: "Google login failed"
                        )
                    }
                }
            )
        }
    }
}

