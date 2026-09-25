package com.oyetech.kmpfeatures.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
actual fun OperatorExampleScreenSetup(
) {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<OperatorExampleOperator>(
        parameters = { parametersOf(operatorScope) },
    )

    OperatorExampleScreenConnection(
        operator = operator,
    )
}
