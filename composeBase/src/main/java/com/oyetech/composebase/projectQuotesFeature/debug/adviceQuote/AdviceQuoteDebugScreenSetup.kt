package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-3.02.2025-
-22:29-
 **/

@Composable
fun AdviceQuoteDebugScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<AdviceQuoteDebugVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    AdviceQuoteDebugScreen(modifier = modifier, uiState = uiState, onEvent = { vm.onEvent(it) })

}

@Composable
fun AdviceQuoteDebugScreen(
    modifier: Modifier = Modifier,
    uiState: AdviceQuoteDebugUiState,
    onEvent: (AdviceQuoteDebugEvent) -> (Unit),
) {
    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}