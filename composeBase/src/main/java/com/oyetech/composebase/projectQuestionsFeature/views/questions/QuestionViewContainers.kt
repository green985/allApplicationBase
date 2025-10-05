package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.languageModule.keyset.LanguageKey

/**
 * Skeleton containers for Question UI. Keep contents empty for now.
 * Receives uiState and onEvent for future wiring.
 */
@Composable
fun QuestionViewScaffoldLayout(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = Modifier.padding(4.dp)) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            QuestionHeaderContainer(uiState = uiState, onEvent = onEvent)
            QuestionTitleDescriptionContainer(uiState = uiState, onEvent = onEvent)
            QuestionAnswerAreaContainer(uiState = uiState, onEvent = onEvent)
            QuestionUserInfoContainer(uiState = uiState, onEvent = onEvent)
            QuestionShareActionsContainer(uiState = uiState, onEvent = onEvent)
        }
    }
}

@Composable
fun QuestionHeaderContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // EMPTY PLACEHOLDER
    }
}

@Composable
fun QuestionTitleDescriptionContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            modifier = Modifier.testTag("titleText"),
            style = MaterialTheme.typography.bodyLarge,
            text = uiState.titleText.ifBlank { LanguageKey.untitledQuestionText }
        )
    }
}

@Composable
fun QuestionAnswerAreaContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Only implement YES/NO (or any two-option case) for now
    if (uiState.options.size == 2) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiState.options.forEachIndexed { index, opt ->
                if (index > 0) Spacer(Modifier.size(8.dp))
                Button(
                    onClick = {
                        onEvent(
                            QuestionViewEvent.OnOptionSelected(
                                uiState.questionId,
                                opt.id,
                            )
                        )
                    },
                    enabled = !uiState.isAnswered
                ) {
                    Text(text = opt.text.ifBlank { opt.id })
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // Not implemented for other categories
        }
    }
}

@Composable
fun QuestionUserInfoContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // EMPTY PLACEHOLDER (user + question info area)
    }
}

@Composable
fun QuestionShareActionsContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // EMPTY PLACEHOLDER (share, etc.)
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionViewScaffoldLayout_Preview() {
    QuestionViewScaffoldLayout(
        uiState = QuestionViewUiState(isLoading = false, titleText = "Sample Question"),
        onEvent = {}
    )
}