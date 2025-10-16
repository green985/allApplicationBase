package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionTagCatalog
import kotlinx.collections.immutable.ImmutableList

@Composable
fun QuestionTagsAreaContainer(
    selectedTags: ImmutableList<QueTag>,
    onEvent: (QuestionViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuestionTagCatalog.ALL.forEach { tag ->
                val isSelected = selectedTags.any { it.id == tag.id }

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onEvent(QuestionViewEvent.OnTagRemoved(tag))
                        } else {
                            onEvent(QuestionViewEvent.OnTagSelected(tag))
                        }
                    },
                    label = {
                        Text(text = tag.name)
                    }
                )
            }
        }
    }
}
