package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import com.oyetech.models.questionProject.questionOperation.QuestionCategories
import com.oyetech.models.questionProject.questionOperation.QuestionOptionCatalog
import com.oyetech.models.questionProject.questionOperation.QuestionOptionCatalog.TwoChoice.YES_NO
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber

@Composable
fun TwoChoicesSelectorView(
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
) {
    val selectedId = uiState.selectedAnswer
    val options = uiState.options

    Row(
        modifier = Modifier
            .fillMaxWidth(QuestionProjectViewAttrs.selectorWidthFraction)
            .clip(RoundedCornerShape(QuestionProjectViewAttrs.cornerRadiusSmall))
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = option.id == selectedId
            val color = when (uiState.category) {
                QuestionCategories.TWO_CHOICE -> when (option) {
                    QuestionOptionCatalog.TwoChoice.YES -> {
                        QuestionAnswerColors.Yes
                    }

                    QuestionOptionCatalog.TwoChoice.NO -> {
                        QuestionAnswerColors.No
                    }

                    QuestionOptionCatalog.TwoChoice.DOWN -> {
                        QuestionAnswerColors.Down
                    }

                    QuestionOptionCatalog.TwoChoice.UP -> {
                        QuestionAnswerColors.Up
                    }

                    QuestionOptionCatalog.TwoChoice.GOOD -> {
                        QuestionAnswerColors.Good
                    }

                    QuestionOptionCatalog.TwoChoice.BAD -> {
                        Timber.d("YesNoSelector: NO selected")
                        QuestionAnswerColors.Bad
                    }

                    else -> {
                        QuestionAnswerColors.Outline
                    }
                }

                else -> if (isSelected) MaterialTheme.colorScheme.primary else QuestionAnswerColors.Outline
            }

            val shape = when (index) {
                0 -> RoundedCornerShape(
                    topStart = QuestionProjectViewAttrs.cornerRadiusSmall,
                    bottomStart = QuestionProjectViewAttrs.cornerRadiusSmall
                )

                options.size - 1 -> RoundedCornerShape(
                    topEnd = QuestionProjectViewAttrs.cornerRadiusSmall,
                    bottomEnd = QuestionProjectViewAttrs.cornerRadiusSmall
                )

                else -> RoundedCornerShape(0.dp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = QuestionProjectViewAttrs.borderWidthThin,
                        color = if (isSelected) color else QuestionAnswerColors.Outline,
                        shape = shape
                    )
                    .background(
                        if (isSelected) {
                            color.copy(alpha = QuestionProjectViewAttrs.selectedBgAlpha)
                        } else Color.Transparent
                    )
                    .clickable {
                        onEvent(
                            QuestionViewEvent.OnOptionSelected(
                                uiState.questionId,
                                option.id
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(QuestionProjectViewAttrs.spacingMd),
                    text = option.text.ifEmpty { option.id }.uppercase(),
                    color = if (uiState.isAnsweredByUser) {
                        QuestionAnswerColors.Disabled
                    } else {
                        color
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
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
        TwoChoicesSelectorView(
            uiState = QuestionViewUiState(
                isLoading = false,
                questionId = "q1",
                selectedAnswer = selected,
                options = YES_NO.toImmutableList()
            ),
            onEvent = {
                if (it is QuestionViewEvent.OnOptionSelected) {
                    selected = it.optionId
                }
            }
        )
    }
}
