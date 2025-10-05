package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.models.questionProject.questionOperation.QueOption
import kotlinx.collections.immutable.toImmutableList

@Composable
fun YesNoSelector(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
) {
    val yesSelected = uiState.selectedAnswer == "YES"
    val noSelected = uiState.selectedAnswer == "NO"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(
                    width = if (yesSelected) 2.dp else 1.dp,
                    color = if (yesSelected) QuestionAnswerColors.Yes else QuestionAnswerColors.Outline,
                    shape = RoundedCornerShape(
                        topStart = 8.dp, bottomStart = 8.dp
                    )
                )
                .clickable {
                    onEvent(
                        QuestionViewEvent.OnOptionSelected(
                            uiState.questionId,
                            "YES"
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "YES",
                color = QuestionAnswerColors.Yes,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(
                    width = if (noSelected) 2.dp else 1.dp,
                    color = if (noSelected) QuestionAnswerColors.No else QuestionAnswerColors.Outline,
                    shape = RoundedCornerShape(
                        topEnd = 8.dp, bottomEnd = 8.dp
                    )
                )
                .clickable {
                    onEvent(
                        QuestionViewEvent.OnOptionSelected(
                            uiState.questionId,
                            "NO"
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NO",
                color = QuestionAnswerColors.No,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun YesNoSelectorPreview() {
    var selected by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF7043), Color(0xFFE91E63))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        YesNoSelector(
            uiState = QuestionViewUiState(
                isLoading = false,
                questionId = "q1",
                selectedAnswer = selected,
                options = listOf(
                    QueOption(id = "YES", text = "YES"),
                    QueOption(id = "NO", text = "NO")
                ).toImmutableList()
            ),
            onEvent = {
                if (it is QuestionViewEvent.OnOptionSelected) {
                    selected = it.optionId
                }
            }
        )
    }
}
