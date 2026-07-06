package com.oyetech.composebase.sharedScreens.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun StopwatchDurationScreenSetup() {
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                top = AppSpacing.xxxl,
                bottom = AppSpacing.sm,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Duration",
            style = AppTextStyles.titleSmall,
        )


        uiState.durations.forEach { item ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(StopwatchDurationEvent.OnDurationSelected(item.session)) },
                contentPadding = PaddingValues(
                    horizontal = AppSpacing.sm,
                    vertical = AppSpacing.xs
                ),
            ) {
                Text(
                    text = item.label,
                    style = AppTextStyles.bodySecondary,
                )
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onEvent(StopwatchDurationEvent.OnExportClicked) },
            contentPadding = PaddingValues(
                horizontal = AppSpacing.sm,
                vertical = AppSpacing.xs
            ),
        ) {
            Text(
                text = "Dışa Aktar",
                style = AppTextStyles.bodySecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StopwatchDurationScreenPreview() {
    MaterialTheme {
        StopwatchDurationScreen(
            uiState = StopwatchDurationUiState(),
            onEvent = {},
        )
    }
}
