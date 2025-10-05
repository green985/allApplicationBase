package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oyetech.models.questionProject.questionOperation.QueOption

@Composable
fun TwoChoiceAnswerView(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = reorderTwoOptionsConsistently(uiState.questionId, uiState.options)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, opt ->
                if (index > 0) Spacer(Modifier.size(10.dp))
                val isSelected = uiState.selectedAnswer == opt.id
                val enabled = !uiState.isAnswered
                val isYesNo =
                    uiState.options.any { it.id == "YES" } && uiState.options.any { it.id == "NO" }
                val bgColor = when {
                    isYesNo && opt.id == "YES" -> Color(0xFF2E7D32) // green 800
                    isYesNo && opt.id == "NO" -> Color(0xFFC62828) // red 800
                    else -> MaterialTheme.colorScheme.primary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .alpha(if (uiState.isAnswered && !isSelected) 0.75f else 1f)
                        .clickable(enabled = true) {
                            onEvent(
                                QuestionViewEvent.OnOptionSelected(
                                    uiState.questionId,
                                    opt.id
                                )
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = (opt.text.ifBlank { opt.id }).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
        // optionsHolder: left bottom - delete icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isAnswered) {
                IconButton(onClick = { onEvent(QuestionViewEvent.OnDeleteAnswerClicked(uiState.questionId)) }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Answer")
                }
            }
        }
    }
}

fun reorderTwoOptionsConsistently(questionId: String, options: List<QueOption>): List<QueOption> {
    val opts = options.toList()
    return if (questionId.hashCode() % 2 == 0) opts else opts.asReversed()
}