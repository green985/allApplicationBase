package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.languageModule.keyset.LanguageKey

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
) {
    val answeredText = when (uiState.selectedAnswer) {
        "YES" -> LanguageKey.answerYes
        "NO" -> LanguageKey.answerNo
        else -> ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
//            .padding(contentPadding)
    ) {
        Text(
            text = uiState.titleText.ifBlank { LanguageKey.untitledQuestionText }
        )
        if (uiState.bodyText.isNotBlank()) {
            Text(text = uiState.bodyText)
        }

        SpacerSmall()

        Row {
            Button(
                onClick = { onEvent(QuestionViewEvent.YesClicked) },
                enabled = !uiState.isAnswered
            ) {
                Text(text = uiState.optionYesText.ifBlank { LanguageKey.yesText })
            }
            SpacerSmall()
            Button(
                onClick = { onEvent(QuestionViewEvent.NoClicked) },
                enabled = !uiState.isAnswered
            ) {
                Text(text = uiState.optionNoText.ifBlank { LanguageKey.noText })
            }
        }

        if (uiState.isAnswered) {
            SpacerSmall()
            Text(text = LanguageKey.yourAnswerText + ": " + answeredText)
        }
    }
}

@Composable
fun SpacerSmall() {
    Box(modifier = Modifier.height(8.dp))
}

@Preview
@Composable
fun QuestionYesNoScreen_Preview_Loading() {
    QuestionYesNoView(
        uiState = QuestionViewUiState(
            isLoading = false,
            titleText = "Enable notifications?",
            bodyText = "Quick poll",
            optionYesText = "Yes",
            optionNoText = "No"
        ),
        onEvent = {},
    )
}

@Preview
@Composable
fun QuestionYesNoScreen_Preview_Answered() {
    QuestionYesNoView(
        uiState = QuestionViewUiState(
            isLoading = false,
            titleText = "Enable dark mode?",
            bodyText = "Vote now",
            optionYesText = "Yes",
            optionNoText = "No",
            isAnswered = true,
            selectedAnswer = "YES"
        ),
        onEvent = {},
    )
}