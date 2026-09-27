package com.oyetech.kmpfeatures.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock

@Composable
fun DiaryScreenSetup() {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<DiaryOperator>(
        parameters = { parametersOf(operatorScope) },
    )
    val uiState by operator.state.collectAsState()

    DiaryScreen(
        uiState = uiState,
        onAction = operator::dispatch,
    )
}

@Composable
fun DiaryScreen(
    uiState: DiaryUiState,
    onAction: (DiaryAction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(DiaryAction.AddEntryClicked) },
            ) {
                Text(StringKeys.addEntry)
            }
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = StringKeys.diary,
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.textPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    enabled = uiState.canGoPrevious,
                    onClick = { onAction(DiaryAction.PreviousDayClicked) },
                ) {
                    Text(StringKeys.yesterday)
                }
                Column {
                    Text(
                        text = uiState.selectedDateLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.textPrimary,
                    )
                    Text(
                        text = if (uiState.isTodaySelected) StringKeys.today else StringKeys.selectedDay,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textSecondary,
                    )
                }
                Button(
                    enabled = uiState.canGoNext,
                    onClick = { onAction(DiaryAction.NextDayClicked) },
                ) {
                    Text(StringKeys.tomorrow)
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.dayQuote,
                onValueChange = { onAction(DiaryAction.QuoteChanged(it)) },
                label = { Text(StringKeys.todaysQuote) },
                placeholder = { Text(StringKeys.writeTodaysQuote) },
                singleLine = true,
            )
            uiState.entryItems.forEach { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { onAction(DiaryAction.EntryEditClicked(entry.id)) },
                )
            }
            Button(onClick = { onAction(DiaryAction.BackClicked) }) {
                Text(StringKeys.back)
            }
        }
    }
    if (uiState.isEditorVisible) {
        Dialog(
            onDismissRequest = { onAction(DiaryAction.EntryDialogDismissed) },
        ) {
            EntryEditor(
                selectedArea = uiState.selectedArea,
                text = uiState.text,
                areaOptions = uiState.areaOptions,
                onAreaSelected = { onAction(DiaryAction.AreaSelected(it)) },
                onTextChange = { onAction(DiaryAction.TextChanged(it)) },
                onSave = { onAction(DiaryAction.SaveEntryClicked) },
            )
        }
    }
}

@Composable
private fun DiaryEntryCard(
    entry: DiaryEntryUiState,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = entry.areaLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.textSecondary,
                )
                Text(
                    text = entry.timeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textSecondary,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun EntryEditor(
    selectedArea: AreaEntry?,
    text: String,
    areaOptions: List<DiaryAreaUiState>,
    onAreaSelected: (AreaEntry) -> Unit,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = StringKeys.newEntry,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.textPrimary,
            )
            Text(
                text = StringKeys.area,
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary,
            )
            areaOptions.chunked(4).forEach { rowAreas ->
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rowAreas.forEachIndexed { index, areaOption ->
                        SegmentedButton(
                            modifier = Modifier.weight(1f),
                            selected = selectedArea == areaOption.area,
                            onClick = { onAreaSelected(areaOption.area) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = rowAreas.size,
                            ),
                        ) {
                            Text(areaOption.label)
                        }
                    }
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onTextChange,
                label = { Text(StringKeys.note) },
                minLines = 3,
            )
            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    enabled = selectedArea != null && text.isNotBlank(),
                    onClick = onSave,
                ) {
                    Text(StringKeys.save)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiaryScreenPreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ViewModuleTheme {
        DiaryScreen(
            uiState = DiaryUiState(
                selectedDate = today,
                isTodaySelected = true,
                selectedDateLabel = "25 Eylül 2026",
                entries = emptyList(),
                entryItems = listOf(
                    DiaryEntryUiState(
                        id = "1",
                        areaLabel = StringKeys.mindArea,
                        createdBy = StringKeys.adminUsername,
                        timeLabel = "21:00",
                        text = StringKeys.sampleMindEntry,
                    ),
                ),
                areaOptions = emptyList(),
                isEditorVisible = false,
                selectedArea = null,
                text = "",
                dayQuote = "",
            ),
            onAction = {},
        )
    }
}
