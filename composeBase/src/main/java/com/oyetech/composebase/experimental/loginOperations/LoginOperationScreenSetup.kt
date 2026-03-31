package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.baseViews.loadingErrors.ErrorDialogFullScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingDialogFullScreen
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent
import com.oyetech.composebase.experimental.authOperation.AuthOperationUiState
import com.oyetech.composebase.experimental.authOperation.AuthOperationVM
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import org.koin.compose.koinInject
import timber.log.Timber

/**
Created by Erdi Özbek
-22.12.2024-
-02:32-
 **/
@Composable
fun LoginOperationScreenSetup(
    uiState: AuthOperationUiState,
    onErrorDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Timber.d("LoginOperationScreenSetup: isLoading=${uiState.isLoading}, isError=${uiState.isError}, errorMessage=${uiState.errorMessage}")
    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (uiState.isLoading) {
            LoadingDialogFullScreen()
        } else if (uiState.isError) {
            ErrorDialogFullScreen(
                errorMessage = uiState.errorMessage,
                onDismiss = onErrorDismiss
            )
        }
    }
}

@Composable
fun LoginOperationSmallButtonSetup() {
    if (LocalInspectionMode.current) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "Login",
                    style = AppTextStyles.label,
                )
            }
        }
    } else {
        val authOperationVM = koinInject<AuthOperationVM>()
        val authUiState by authOperationVM.authOperationState.collectAsState()

        if (!authUiState.isLogin) {
            Row(modifier = Modifier.size(100.dp)) {
                Button(
                    onClick = { authOperationVM.onEvent(AuthOperationEvent.LoginClicked) }
                ) {
                    Text(
                        text = if (authUiState.isLoading) "Loading..." else "Login",
                        style = AppTextStyles.label,
                    )
                }
            }
        }
    }
}

// preview
@Preview(showBackground = true)
@Composable
fun LoginOperationSmallButtonSetupPreview() {
    LoginOperationSmallButtonSetup()
}
