package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oyetech.composebase.baseViews.loadingErrors.ErrorDialogFullScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingDialogFullScreen

/**
Created by Erdi Özbek
-22.12.2024-
-02:32-
 **/
@Composable
fun LoginOperationScreenSetup(
    uiState: LoginOperationUiState,
    onErrorDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
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