package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-22.12.2024-
-02:32-
 **/

@Composable
fun LoginOperationScreenSetup() {
    val vm = koinViewModel<LoginOperationVM>()

    val uiState by vm.uiState.collectAsState()
}