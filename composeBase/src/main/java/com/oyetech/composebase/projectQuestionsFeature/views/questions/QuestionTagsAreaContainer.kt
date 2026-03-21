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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            .padding(vertical = 8.dp)
    ) {
        if (isCreateQuestion) {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
