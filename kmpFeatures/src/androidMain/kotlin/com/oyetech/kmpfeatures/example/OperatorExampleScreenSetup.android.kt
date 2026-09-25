package com.oyetech.kmpfeatures.example

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual fun OperatorExampleScreenSetup(
    onBackClick: () -> Unit,
) {
    val viewModel = viewModel<OperatorExampleViewModel>()

    OperatorExampleScreenConnection(
        operator = viewModel,
        onBackClick = onBackClick,
    )
}
