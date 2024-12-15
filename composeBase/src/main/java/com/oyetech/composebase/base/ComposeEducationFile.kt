package com.oyetech.composebase.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalErrorView
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalLoadingView
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiEvent
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

// UIState class for Login Screen
data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
) : UIState()

// UIEvent sealed class for Login actions
sealed class LoginUIEvent {
    data class EmailChanged(val email: String) : LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()
    object SubmitLogin : LoginUIEvent()
}

// ViewModel for Login Screen
class LoginViewModel(dispatcher: AppDispatchers) : BaseViewModel(dispatcher) {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState

    fun handleEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                _uiState.updateState { copy(email = event.email) }
            }

            is LoginUIEvent.PasswordChanged -> {
                _uiState.updateState { copy(password = event.password) }
            }

            LoginUIEvent.SubmitLogin -> {
                submitLogin()
            }
        }
    }

    private fun submitLogin() {
        _uiState.updateState { copy(isLoading = true) }

        viewModelScope.launch(getDispatcherIo()) {
            try {
                kotlinx.coroutines.delay(1000) // Simulate a login delay
                _uiState.updateState { copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                Timber.e("Login failed: ${e.message}")
                _uiState.updateState {
                    copy(isLoading = false, errorMessage = "Login failed, please try again.")
                }
            }
        }
    }
}

// Composable function for Login Screen UI
@Composable
fun LoginScreen() {
    val viewModel = koinViewModel<LoginViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScaffold(showTopBar = false, showBottomBar = false) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Email field
            BasicTextField(
                value = uiState.email,
                onValueChange = { viewModel.handleEvent(LoginUIEvent.EmailChanged(it)) },
                modifier = Modifier.padding(8.dp),
                decorationBox = { innerTextField ->
                    if (uiState.email.isEmpty()) {
                        Text(text = "Email")
                    }
                    innerTextField()
                }
            )

            // Password field
            BasicTextField(
                value = uiState.password,
                onValueChange = { viewModel.handleEvent(LoginUIEvent.PasswordChanged(it)) },
                modifier = Modifier.padding(8.dp),
                decorationBox = { innerTextField ->
                    if (uiState.password.isEmpty()) {
                        Text(text = "Password")
                    }
                    innerTextField()
                }
            )

            // Loading indicator
            if (uiState.isLoading) {
                GlobalLoadingView()
            }

            // Error message
            uiState.errorMessage?.let {
                GlobalErrorView(
                    uiState = GlobalLoadingUiState.Error(it),
                    onEvent = { event ->
                        when (event) {
                            GlobalLoadingUiEvent.Dismiss -> viewModel.handleEvent(
                                LoginUIEvent.EmailChanged(
                                    ""
                                )
                            )

                            GlobalLoadingUiEvent.Retry -> viewModel.handleEvent(LoginUIEvent.SubmitLogin)
                        }
                    }
                )
            }

            // Login button
            Button(
                onClick = { viewModel.handleEvent(LoginUIEvent.SubmitLogin) },
                modifier = Modifier.padding(8.dp),
                enabled = !uiState.isLoading
            ) {
                Text(text = "Login")
            }
        }
    }
}

//
//open class BaseViewModel(private val dispatcher: AppDispatchers) : ViewModel() {
//
//    val navigationRouteStateFlow = MutableStateFlow("")
//
//    fun navigateTo(route: Route) {
//        navigationRouteStateFlow.value = route.route
//    }
//
//    fun getDispatcher() = dispatcher
//}
