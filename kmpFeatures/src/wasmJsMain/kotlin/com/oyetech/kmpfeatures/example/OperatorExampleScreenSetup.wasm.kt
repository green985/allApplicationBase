package com.oyetech.kmpfeatures.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
actual fun OperatorExampleScreenSetup(
    onBackClick: () -> Unit,
) {
    val operatorScope = rememberCoroutineScope()
    val operator = remember(operatorScope) {
        OperatorExampleOperator(operatorScope)
    }

    OperatorExampleScreenConnection(
        operator = operator,
        onBackClick = onBackClick,
    )
}
