package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalErrorView
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalLoadingView
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-22.12.2024-
-02:32-
 **/

@Composable
fun LoginOperationScreenSetup(navigationRoute: (navigationRoute: String) -> Unit = {}) {
    val vm = koinViewModel<LoginOperationVM>()

    val uiState by vm.loginOperationState.collectAsStateWithLifecycle()


    LoginOperationScreen(uiState = uiState, onEvent = { vm.handleEvent(it) })


    if (uiState.isLogin) {
        if (uiState.displayNameRemote.isBlank()) {
            LaunchedEffect(uiState.displayNameRemote) {
                navigationRoute.invoke(RadioAppProjectRoutes.CompleteProfileScreen.route)
            }
        }
    }

    if (uiState.isLoading) {
        GlobalLoadingView()
    } else if (uiState.isError) {
        GlobalErrorView(
            message = uiState.errorMessage,
            onDismiss = { vm.handleEvent(LoginOperationEvent.ErrorDismiss) },
            onRetry = { vm.handleEvent(LoginOperationEvent.LoginClicked) }
        )
    }
}

@Composable
fun LoginOperationScreen(uiState: LoginOperationUiState, onEvent: (LoginOperationEvent) -> Unit) {
    BaseScaffold {
        Column {
            Spacer(modifier = Modifier.height(64.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Login Operation Screen == " + uiState.toString())
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button({ onEvent.invoke(LoginOperationEvent.LoginClicked) }) {
                Text(text = LanguageKey.login)
            }
        }

    }
}
