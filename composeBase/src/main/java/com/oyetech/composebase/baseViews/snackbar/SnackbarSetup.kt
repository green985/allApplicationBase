package com.oyetech.composebase.baseViews.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

/**
Created by Erdi Özbek
-1.01.2025-
-23:22-
 **/

@Composable
fun SnacbarScreenSetup(snackbarHostState: SnackbarHostState) {
    val snackbarDelegate = koinInject<SnackbarDelegate>()

    val stateee by snackbarDelegate.snacbarUiState.collectAsStateWithLifecycle()

    DefaultSnackbar(snackbarHostState)


    LaunchedEffect(stateee.uuid) {
        if (stateee.message.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = stateee.message,
                actionLabel = stateee.actionLabel,
            )
        }

    }
}