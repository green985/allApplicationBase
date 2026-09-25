package com.oyetech.kmpfeatures.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpfeatures.auth.googleWebClientId
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import org.koin.compose.koinInject

@Composable
fun LoginScreenSetup(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinInject(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        uiState = state,
        onEvent = { event ->
            when (event) {
                LoginEvent.GoogleLoginClicked -> viewModel.login(googleWebClientId())
                LoginEvent.LocalAdminLoginClicked -> viewModel.loginAsLocalAdmin()
                LoginEvent.BackClicked -> onBackClick()
                LoginEvent.ErrorDismissed -> viewModel.clearError()
            }
        },
    )
}

sealed interface LoginEvent {
    data object GoogleLoginClicked : LoginEvent
    data object LocalAdminLoginClicked : LoginEvent
    data object BackClicked : LoginEvent
    data object ErrorDismissed : LoginEvent
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Google ile giriş",
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.textPrimary,
        )
        Button(
            enabled = !uiState.isLoading,
            onClick = { onEvent(LoginEvent.GoogleLoginClicked) },
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Google ile devam et")
            }
        }
        Button(
            enabled = !uiState.isLoading,
            onClick = { onEvent(LoginEvent.LocalAdminLoginClicked) },
        ) {
            Text("Admin giriş")
        }
        if (uiState.username.isNotBlank()) {
            Text(
                text = "Kullanıcı: ${uiState.username}",
                color = AppColors.textPrimary,
            )
        }
        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = AppColors.error,
            )
        }
        Button(onClick = { onEvent(LoginEvent.BackClicked) }) {
            Text("Geri")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    ViewModuleTheme {
        LoginScreen(
            uiState = LoginUiState(username = "Admin"),
            onEvent = {},
        )
    }
}
