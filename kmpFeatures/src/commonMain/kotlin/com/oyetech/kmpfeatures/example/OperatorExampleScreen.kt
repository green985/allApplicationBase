package com.oyetech.kmpfeatures.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.FeatureOperator
import com.oyetech.kmpmodels.ui.event.OperatorExampleAction
import com.oyetech.kmpmodels.ui.event.OperatorExampleEffect
import com.oyetech.kmpmodels.ui.state.OperatorExampleUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import org.koin.compose.koinInject

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
    val snackbarDelegate = koinInject<SnackbarDelegate>()
    val navigationUseCase = koinInject<NavigationUseCase>()

    LaunchedEffect(operator) {
        operator.effects.collect { effect ->
            when (effect) {
                is OperatorExampleEffect.ShowMessage -> {
                    snackbarDelegate.triggerSnackbarState(effect.message)
                }

                OperatorExampleEffect.NavigateBack -> navigationUseCase.goBack()
            }
        }
    }

    OperatorExampleScreen(
        uiState = uiState,
        onAction = operator::dispatch,
    )
}

@Composable
fun OperatorExampleScreen(
    uiState: OperatorExampleUiState,
    onAction: (OperatorExampleAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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

@Preview(showBackground = true)
@Composable
private fun OperatorExampleScreenPreview() {
    ViewModuleTheme {
        OperatorExampleScreen(
            uiState = OperatorExampleUiState(operationCount = 2),
            onAction = {},
        )
    }
}
