package com.oyetech.composebase.experimental.moonOperation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oyetech.composebase.base.BaseScaffold

/**
Created by Erdi Özbek
-8.07.2025-
-00:17-
 **/

@Suppress("FunctionName")
@Composable
fun MoonOperationScreen(
    modifier: Modifier = Modifier,
    uiState: MoonOperationUiState,
    onEvent: (MoonOperationEvent) -> (Unit),
) {
    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}