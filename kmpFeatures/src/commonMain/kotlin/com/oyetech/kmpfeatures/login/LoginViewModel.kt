package com.oyetech.kmpfeatures.login

import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val username: String = "",
    val errorMessage: String = "",
)

class LoginViewModel(
    private val googleLoginOperation: GoogleLoginOperation,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(clientId: String) {
        if (clientId.isBlank() || clientId.startsWith("REPLACE_")) {
            _uiState.update { it.copy(errorMessage = "Google Web Client ID yapılandırılmamış") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            googleLoginOperation.login(clientId).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            username = user.username ?: "daha belli degil !",
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Google login failed",
                        )
                    }
                },
            )
        }
    }

    fun clear() {
        scope.cancel()
    }
}
