package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionTagCatalog

@Composable
fun QuestionFilterBar(
    modifier: Modifier = Modifier,
    currentFilter: QueFilter,
    onEvent: (QuestionListEvent) -> Unit,
    isAdminMode: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (isAdminMode) {
            AdminFilterRow(
                currentAdminFilter = currentFilter.adminFilterType,
                onFilterChanged = { onEvent(QuestionListEvent.OnAdminFilterChanged(it)) }
            )
        }

        TagFilterRow(
            selectedTag = currentFilter.selectedTagFilter,
            onTagSelected = { onEvent(QuestionListEvent.OnTagFilterChanged(it)) }
        )

        HorizontalDivider()
    }
}

@Composable
private fun AdminFilterRow(
    currentAdminFilter: QuestionListAdminFilterType,
    onFilterChanged: (QuestionListAdminFilterType) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Admin Filter",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                QuestionListAdminFilterType.NONE to "All",
                QuestionListAdminFilterType.PENDING_ADMIN to "Pending",
                QuestionListAdminFilterType.APPROVED_ADMIN to "Approved",
                QuestionListAdminFilterType.DECLINED_ADMIN to "Declined",
            )

            filters.forEach { (filter, label) ->
                FilterChip(
                    selected = currentAdminFilter == filter,
                    onClick = { onFilterChanged(filter) },
                    label = { Text(text = label) }
                )
            }
        }
    }
}

@Composable
private fun TagFilterRow(
    selectedTag: QueTag?,
    onTagSelected: (QueTag?) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Tag Filter",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTag == null,
                onClick = { onTagSelected(null) },
                label = { Text(text = "All Tags") }
            )

            QuestionTagCatalog.createQuestionTagList.forEach { tag ->
                FilterChip(
                    selected = selectedTag?.id == tag.id,
                    onClick = { onTagSelected(tag) },
                    label = { Text(text = tag.name) }
                )
            }
        }
    }
}
