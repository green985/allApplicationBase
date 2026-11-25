package com.oyetech.kmpfeatures.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryBulkAction
import com.oyetech.kmpmodels.ui.state.DiaryBulkUiState
import com.oyetech.viewmodule.AppColors
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun DiaryBulkScreenSetup() {
    val scope = rememberCoroutineScope()
    val operator = koinInject<DiaryBulkOperator>(parameters = { parametersOf(scope) })
    val state by operator.state.collectAsState()
    DiaryBulkScreen(state, operator::dispatch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryBulkScreen(
    uiState: DiaryBulkUiState,
    onAction: (DiaryBulkAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(StringKeys.bulkEntryTitle) },
                navigationIcon = {
                    IconButton(
                        enabled = !uiState.isLoading,
                        onClick = { onAction(DiaryBulkAction.BackClicked) },
                    ) {
                        val iconColor = AppColors.textPrimary
                        Icon(
                            imageVector = remember(iconColor) {
                                ImageVector.Builder(
                                    name = "Back",
                                    defaultWidth = 24.dp,
                                    defaultHeight = 24.dp,
                                    viewportWidth = 24f,
                                    viewportHeight = 24f,
                                    autoMirror = true,
                                ).apply {
                                    path(fill = SolidColor(iconColor)) {
                                        moveTo(20f, 11f)
                                        lineTo(7.83f, 11f)
                                        lineTo(13.42f, 5.41f)
                                        lineTo(12f, 4f)
                                        lineTo(4f, 12f)
                                        lineTo(12f, 20f)
                                        lineTo(13.42f, 18.59f)
                                        lineTo(7.83f, 13f)
                                        lineTo(20f, 13f)
                                        close()
                                    }
                                }.build()
                            },
                            contentDescription = StringKeys.back,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.json,
                onValueChange = { onAction(DiaryBulkAction.JsonChanged(it)) },
                enabled = !uiState.isLoading,
                minLines = 10,
                maxLines = 14,
                label = { Text(StringKeys.bulkEntryJsonLabel) },
            )
            Button(
                enabled = uiState.canPreview,
                onClick = { onAction(DiaryBulkAction.PreviewClicked) },
            ) {
                Text(StringKeys.bulkEntryPreview)
            }
            if (uiState.previewItems.isNotEmpty()) {
                Text(StringKeys.bulkEntryPreviewTitle, style = MaterialTheme.typography.titleMedium)
                Text(StringKeys.bulkEntryPreviewHint)
            }
            uiState.previewItems.forEach { item ->
                Column {
                    Text(item.destinationLabel, style = MaterialTheme.typography.titleSmall)
                    Text(item.details)
                    if (item.text.isNotEmpty()) Text(item.text)
                }
            }
            Button(
                enabled = uiState.canSubmit,
                onClick = { onAction(DiaryBulkAction.SubmitClicked) },
            ) {
                Text(StringKeys.bulkEntrySubmit)
            }
            if (uiState.isLoading) CircularProgressIndicator()
            if (uiState.errorMessage.isNotEmpty()) Text(uiState.errorMessage)
            if (uiState.resultItems.isNotEmpty()) Text(StringKeys.bulkEntrySuccess)
            uiState.resultItems.forEach { Text(it) }
            Text(StringKeys.bulkEntryTagsTitle, style = MaterialTheme.typography.titleMedium)
            if (uiState.isLoadingTags) CircularProgressIndicator()
            if (uiState.tagsErrorMessage.isNotEmpty()) {
                Text(uiState.tagsErrorMessage)
                TextButton(onClick = { onAction(DiaryBulkAction.RetryTagsClicked) }) {
                    Text(StringKeys.retry)
                }
            }
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.tagGroups.forEach { group ->
                        Column {
                            Text(group.areaLabel, style = MaterialTheme.typography.titleSmall)
                            Text(group.tagsLabel)
                        }
                    }
                    Text(StringKeys.bulkEntryExampleTitle, style = MaterialTheme.typography.titleMedium)
                    Text(StringKeys.bulkEntryExampleHint)
                    Text(uiState.exampleJson, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
