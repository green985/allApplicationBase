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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.LoginAction
import com.oyetech.kmpmodels.ui.state.LoginUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun LoginScreenSetup() {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<LoginOperator>(
        parameters = { parametersOf(operatorScope) },
    )
    val state by operator.state.collectAsState()

    LoginScreen(
        uiState = state,
        onAction = operator::dispatch,
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = StringKeys.googleLogin,
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.textPrimary,
        )
        Button(
            enabled = !uiState.isLoading,
            onClick = { onAction(LoginAction.GoogleLoginClicked) },
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(StringKeys.continueWithGoogle)
            }
        }
        Button(
            enabled = !uiState.isLoading,
            onClick = { onAction(LoginAction.LocalAdminLoginClicked) },
        ) {
            Text(StringKeys.adminLogin)
        }
        if (uiState.username.isNotBlank()) {
            Text(
                text = StringKeys.username(uiState.username),
                color = AppColors.textPrimary,
            )
        }
        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = AppColors.error,
            )
        }
        Button(onClick = { onAction(LoginAction.BackClicked) }) {
            Text(StringKeys.back)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    ViewModuleTheme {
        LoginScreen(
            uiState = LoginUiState(username = StringKeys.adminUsername),
            onAction = {},
        )
    }
}
