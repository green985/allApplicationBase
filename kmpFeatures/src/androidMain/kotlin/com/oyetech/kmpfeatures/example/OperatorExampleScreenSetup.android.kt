package com.oyetech.kmpfeatures.example

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun OperatorExampleScreenSetup(
    onBackClick: () -> Unit,
) {
    val viewModel = koinViewModel<OperatorExampleViewModel>()

    OperatorExampleScreenConnection(
        operator = viewModel,
        onBackClick = onBackClick,
    )
}
