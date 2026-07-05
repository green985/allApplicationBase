package com.oyetech.composebase.sharedScreens.stopwatch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.projectQuestionsFeature.theme.AppSpacing
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.domain.repository.stopwatch.StopwatchTag
import org.koin.androidx.compose.koinViewModel

@Composable
fun StopwatchScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<StopwatchVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    StopwatchScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
    )
}

@Composable
fun StopwatchScreen(
    modifier: Modifier = Modifier,
    uiState: StopwatchUiState,
    onEvent: (StopwatchEvent) -> Unit,
) {
    BackHandler(enabled = true) { /* back press disabled on timer screen */ }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.xxl, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = uiState.formattedTime,
            style = AppTextStyles.titleLarge,
        )

        if (uiState.suggestedTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Etiket",
                style = AppTextStyles.label,
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                uiState.suggestedTags.forEach { tag ->
                    FilterChip(
                        selected = uiState.selectedTag == tag,
                        onClick = { onEvent(StopwatchEvent.OnTagSelected(tag)) },
                        label = { Text(text = tag.displayLabel()) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onEvent(StopwatchEvent.OnCancelClicked) },
            contentPadding = PaddingValues(
                horizontal = AppSpacing.sm,
                vertical = AppSpacing.xs,
            ),
        ) {
            Text(
                text = "Cancel",
                style = AppTextStyles.bodySecondary,
            )
        }
    }
}

private fun StopwatchTag.displayLabel(): String = when (this) {
    StopwatchTag.KAHVALTI -> "Kahvaltı"
    StopwatchTag.SIGARA -> "Sigara"
    StopwatchTag.MEDITASYON -> "Meditasyon"
    StopwatchTag.YEMEK_HAZIRLAMA -> "Yemek Hazırlama"
    StopwatchTag.YEMEK_YEME -> "Yemek Yeme"
}

@Preview(showBackground = true)
@Composable
private fun StopwatchScreenPreview() {
    MaterialTheme {
        StopwatchScreen(
            uiState = StopwatchUiState(formattedTime = "19:45"),
            onEvent = {},
        )
    }
}
