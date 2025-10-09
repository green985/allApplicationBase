package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.questionProject.questionOperation.QuestionType

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
    OutlinedCard(modifier = Modifier.padding(QuestionProjectViewAttrs.spacingXs)) {
        Column(
            modifier = modifier
                .padding(QuestionProjectViewAttrs.spacingSm)
                .background(MaterialTheme.colorScheme.background)
        ) {
            QuestionHeaderContainer(uiState = uiState, onEvent = onEvent)
            QuestionTitleDescriptionContainer(uiState = uiState, onEvent = onEvent)
            QuestionAnswerAreaContainer(uiState = uiState, onEvent = onEvent)
            QuestionOptionsAreaContainer(uiState = uiState, onEvent = onEvent)
            QuestionUserInfoContainer(uiState = uiState, onEvent = onEvent)
            QuestionShareActionsContainer(uiState = uiState, onEvent = onEvent)
        }
    }
}

@Composable
fun QuestionOptionsAreaContainer(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
) {
    // optionsHolder: left bottom - delete icon
    if (uiState.isAnsweredByUser) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onEvent(QuestionViewEvent.OnDeleteAnswerClicked(uiState.questionId)) }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Answer")
            }
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
            .padding(vertical = QuestionProjectViewAttrs.spacingXs)
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
            .padding(vertical = QuestionProjectViewAttrs.spacingXs)
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
    when (uiState.questionType) {
        QuestionType.SINGLE_CHOICE -> {
            if (uiState.options.size == 2) {
                TwoChoicesSelectorView(uiState = uiState, onEvent = onEvent)
            } else {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = QuestionProjectViewAttrs.spacingXs)
                ) {
                    // TODO: SINGLE_CHOICE (N) renderer
                }
            }
        }
//        QuestionType.MULTI_CHOICE -> {
//            Box(
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//            ) {
//                // TODO: MULTI_CHOICE renderer
//            }
//        }
//        QuestionType.SCALE -> {
//            Box(
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//            ) {
//                // TODO: SCALE renderer
//            }
//        }
//        QuestionType.OPEN_ENDED -> {
//            Box(
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//            ) {
//                // TODO: OPEN_ENDED renderer
//            }
//        }
    }
}

private fun reorderTwoOptionsConsistently(uiState: QuestionViewUiState): List<com.oyetech.models.questionProject.questionOperation.QueOption> {
    val opts = uiState.options.toList()
    return if (uiState.questionId.hashCode() % 2 == 0) opts else opts.reversed()
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
            .padding(vertical = QuestionProjectViewAttrs.spacingXs)
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
            .padding(vertical = QuestionProjectViewAttrs.spacingXs)
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