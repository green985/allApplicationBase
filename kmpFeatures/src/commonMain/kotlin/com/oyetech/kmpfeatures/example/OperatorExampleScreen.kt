package com.oyetech.kmpfeatures.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpfeatures.operator.FeatureOperator
import com.oyetech.kmpmodels.ui.event.OperatorExampleAction
import com.oyetech.kmpmodels.ui.event.OperatorExampleEffect
import com.oyetech.kmpmodels.ui.state.OperatorExampleUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme

@Composable
expect fun OperatorExampleScreenSetup(
    onBackClick: () -> Unit,
)

@Composable
internal fun OperatorExampleScreenConnection(
    operator: FeatureOperator<OperatorExampleUiState, OperatorExampleAction, OperatorExampleEffect>,
    onBackClick: () -> Unit,
) {
    val uiState by operator.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operator) {
        operator.effects.collect { effect ->
            when (effect) {
                is OperatorExampleEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                OperatorExampleEffect.NavigateBack -> onBackClick()
            }
        }
    }

    OperatorExampleScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = operator::dispatch,
    )
}

@Composable
fun OperatorExampleScreen(
    uiState: OperatorExampleUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (OperatorExampleAction) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "Operator example",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.textPrimary,
            )
            Text(
                text = "Completed operations: ${uiState.operationCount}",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textSecondary,
            )

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            Button(
                enabled = !uiState.isLoading,
                onClick = { onAction(OperatorExampleAction.RunOperationClicked) },
            ) {
                Text("Run operation")
            }
            OutlinedButton(onClick = { onAction(OperatorExampleAction.ResetClicked) }) {
                Text("Reset")
            }
            OutlinedButton(onClick = { onAction(OperatorExampleAction.BackClicked) }) {
                Text("Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OperatorExampleScreenPreview() {
    ViewModuleTheme {
        OperatorExampleScreen(
            uiState = OperatorExampleUiState(operationCount = 2),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
        )
    }
}
