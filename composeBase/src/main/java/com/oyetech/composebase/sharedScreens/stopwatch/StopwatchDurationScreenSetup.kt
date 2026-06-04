package com.oyetech.composebase.sharedScreens.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.projectQuestionsFeature.theme.AppSpacing
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import org.koin.androidx.compose.koinViewModel

@Composable
fun StopwatchDurationScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<StopwatchDurationVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    StopwatchDurationScreen(
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
    )
}

@Composable
fun StopwatchDurationScreen(
    modifier: Modifier = Modifier,
    uiState: StopwatchDurationUiState,
    onEvent: (StopwatchDurationEvent) -> Unit,
) {
    val durations = listOf(2, 5, 10, 15, 20)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Select Duration",
            style = AppTextStyles.titleLarge,
        )

        durations.forEach { minutes ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(StopwatchDurationEvent.OnDurationSelected(minutes)) },
            ) {
                Text(
                    text = "$minutes minutes",
                    style = AppTextStyles.button,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StopwatchDurationScreenPreview() {
    StopwatchDurationScreen(
        uiState = StopwatchDurationUiState(),
        onEvent = {},
    )
}

