package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.ErrorDismiss
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent.LoginClicked
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
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

    if (uiState.isLogin) {
        if (uiState.displayNameRemote.isBlank()) {
            LaunchedEffect(uiState.displayNameRemote) {
                navigationRoute.invoke(RadioAppProjectRoutes.CompleteProfileScreen.route)
            }
        }
    }

    if (uiState.isLoading) {
        LoadingScreenFullSize()
    } else if (uiState.isError) {
        ErrorScreenFullSize(
            errorMessage = uiState.errorMessage,
            onDismiss = { vm.handleEvent(ErrorDismiss) },
            onRetry = { vm.handleEvent(LoginClicked) },
        )
    }
}

@Composable
fun LoginOperationScreen(uiState: LoginOperationUiState, onEvent: (LoginOperationEvent) -> Unit) {
    BaseScaffold {
        Column {
//            Spacer(modifier = Modifier.height(64.dp))
//            Row(modifier = Modifier.fillMaxWidth()) {
//
//                if (uiState.isUserDeleted) {
//                    Text(text = LanguageKey.accountDeleted + uiState.toString())
//                } else {
//                    Text(text = "Login Operation Screen == " + uiState.toString())
//                }
//            }
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button({ onEvent.invoke(LoginOperationEvent.LoginClicked) }) {
//                Text(text = LanguageKey.login)
//            }
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button({ onEvent.invoke(LoginOperationEvent.DeleteAccountClick) }) {
//                Text(text = LanguageKey.deleteAccountButtonText)
//            }
        }

    }
}
