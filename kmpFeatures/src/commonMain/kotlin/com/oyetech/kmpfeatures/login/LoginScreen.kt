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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.kmpfeatures.auth.googleWebClientId
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    viewModel: LoginViewModel = koinInject(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Google ile giriş", style = MaterialTheme.typography.headlineMedium)
        Button(
            enabled = !state.isLoading,
            onClick = { viewModel.login(googleWebClientId()) },
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Google ile devam et")
            }
        }
        if (state.username.isNotBlank()) {
            Text("Kullanıcı: ${state.username}")
        }
        if (state.errorMessage.isNotBlank()) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Button(onClick = onBackClick) {
            Text("Geri")
        }
    }
}
