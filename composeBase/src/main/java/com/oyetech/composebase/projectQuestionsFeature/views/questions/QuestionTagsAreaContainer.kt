package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oyetech.composebase.projectQuestionsFeature.theme.AppSpacing
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.models.questionProject.questionOperation.QuestionTagCatalog

@Composable
fun QuestionTagsAreaContainer(
    modifier: Modifier = Modifier,
    uiState: QuestionViewUiState,
    onEvent: (QuestionViewEvent) -> Unit,
    isCreateQuestion: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm)
    ) {
        if (isCreateQuestion) {
            Text(
                text = "Tags",
                style = AppTextStyles.label,
                modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
            )
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            if (isCreateQuestion) {

                QuestionTagCatalog.createQuestionTagList.forEach { tag ->
                    val isSelected = uiState.selectedTags.any { it.id == tag.id }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                onEvent(QuestionViewEvent.OnTagRemoved(tag))
                            } else {
                                onEvent(QuestionViewEvent.OnTagSelectedForCreateQuestion(tag))
                            }
                        },
                        label = {
                            Text(text = tag.name)
                        }
                    )
                }
            } else {
                uiState.selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { onEvent.invoke(QuestionViewEvent.OnTagSelected(tag)) },
                        label = {
                            Text(text = tag.name)
                        }
                    )
                }
            }
        }
    }
}
