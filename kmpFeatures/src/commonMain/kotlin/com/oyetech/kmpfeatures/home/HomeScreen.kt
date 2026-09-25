package com.oyetech.kmpfeatures.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpmodels.ui.event.HomeAction
import com.oyetech.kmpmodels.ui.state.HomeUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreenSetup() {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<HomeOperator>(
        parameters = { parametersOf(operatorScope) },
    )
    val uiState by operator.state.collectAsState()

    HomeScreen(
        uiState = uiState,
        onAction = operator::dispatch,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "KMP Features",
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.textPrimary,
        )
        Text(
            text = uiState.welcomeMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.textSecondary,
        )
        Button(onClick = { onAction(HomeAction.AdminLoginClicked) }) {
            Text("Admin giriş")
        }
        Button(onClick = { onAction(HomeAction.OperatorExampleClicked) }) {
            Text("Operator örneğini aç")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ViewModuleTheme {
        HomeScreen(
            uiState = HomeUiState(welcomeMessage = "KMP Features is ready"),
            onAction = {},
        )
    }
}
