package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.oyetech.composebase.baseViews.snackbar.SnacbarScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.generalPlayground.GeneralPlaygroundVm
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
Created by Erdi Özbek
-15.12.2024-
-14:03-
 **/

@Composable
fun GeneralOperationScreenSetup(
    content: @Composable () -> Unit,
) {
    val viewModel = koinViewModel<GeneralOperationVM>()
    val generalPlaygroundVm = koinViewModel<GeneralPlaygroundVm>()

    generalPlaygroundVm.initt()

    val loginOperationVM = koinInject<LoginOperationVM>()

    val loginUiState by loginOperationVM.loginOperationState.collectAsState()

    LoginOperationScreenSetup(
        uiState = loginUiState,
        onErrorDismiss = { loginOperationVM.onEvent(LoginOperationEvent.ErrorDismiss) }
    ) {
        GeneralOperationScreen {
            content()
        }
    }
}

@Composable
fun GeneralOperationScreen(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnacbarScreenSetup(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content()
            }
        })
}