package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.oyetech.models.questionProject.questionOperation.QueOptionsValues
import kotlinx.collections.immutable.toImmutableList

/**
Created by Erdi Özbek
-3.10.2025-
-01:08-
 **/

@Composable
fun QuestionYesNoView(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
) {
    if (isEditMode) {
        CreateQuestionYesNoView(
            uiState = uiState,
            onEvent = onEvent,
            modifier = modifier
        )
    } else {
        YesNoQuestionContent(uiState = uiState, onEvent = onEvent, modifier = modifier)
    }
}

@Composable
private fun YesNoQuestionContent(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val answeredText = when (uiState.selectedAnswer) {
        "YES" -> LanguageKey.answerYes
        "NO" -> LanguageKey.answerNo
        else -> ""
    }

    OutlinedCard(modifier = Modifier.padding(4.dp)) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.testTag("titleText"),
                    style = MaterialTheme.typography.bodyLarge,
                    text = uiState.titleText.ifBlank { LanguageKey.untitledQuestionText }
                )
            }

            SpacerSmall()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.options.isNotEmpty()) {
                    uiState.options.forEachIndexed { index, opt ->
                        if (index > 0) Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                onEvent(
                                    QuestionViewEvent.OnOptionSelected(
                                        uiState.questionId,
                                        opt.id
                                    )
                                )
                            },
                            enabled = !uiState.isAnswered
                        ) {
                            Text(text = opt.text.ifBlank { opt.id })
                        }
                    }
                } else {
//                    Button(
//                        onClick = { onEvent(QuestionViewEvent.OptionSelected("YES")) },
//                        enabled = !uiState.isAnswered
//                    ) {
//                        Text(text = uiState.optionYesText.ifBlank { LanguageKey.yesText })
//                    }
//                    Spacer(Modifier.size(8.dp))
//                    Button(
//                        onClick = { onEvent(QuestionViewEvent.OptionSelected("NO")) },
//                        enabled = !uiState.isAnswered
//                    ) {
//                        Text(text = uiState.optionNoText.ifBlank { LanguageKey.noText })
//                    }
                }
            }

            if (uiState.isAnswered) {
                SpacerSmall()
                Text(text = LanguageKey.yourAnswerText + ": " + answeredText)
            }

        }
    }
}

@Composable
fun CreateQuestionYesNoView(
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
            SpacerSmall()
            // Only titleText is editable
            androidx.compose.material3.OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.titleText,
                onValueChange = { onEvent(QuestionViewEvent.TitleChanged(it)) },
                label = { Text(LanguageKey.untitledQuestionText) }
            )
            SpacerSmall()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { onEvent(QuestionViewEvent.SubmitClicked) }) {
                    Text(text = LanguageKey.save)
                }
                Spacer(Modifier.size(8.dp))
                Button(onClick = { onEvent(QuestionViewEvent.CancelClicked) }) {
                    Text(text = LanguageKey.cancel)
                }
            }
        }
    }
}

@Composable
fun SpacerSmall() {
    Box(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionYesNoScreen_Preview_Answered() {
    QuestionYesNoView(
        uiState = QuestionViewUiState(
            isLoading = false,
            titleText = "Enable dark mode?",
            bodyText = "Vote now",
            isAnswered = true,
            selectedAnswer = "YES"
        ),
        onEvent = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuestionYesNoScreen_Preview_Edit_Mode() {

    QuestionYesNoView(
        uiState = QuestionViewUiState(
            isLoading = false,
            titleText = "Enable dark mode?",
            bodyText = "Vote now",
            isAnswered = true,
            selectedAnswer = "YES"
        ),
        onEvent = {},
        isEditMode = true,
    )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionYesNoScreen_Preview_Loading() {
    QuestionYesNoView(
        uiState = QuestionViewUiState(
            isLoading = false,
            titleText = "Enable notifications?",
            bodyText = "Quick poll",
            options = QueOptionsValues.queYesNoQuestionOptionList.toImmutableList()
        ),
        onEvent = {},
    )
}
