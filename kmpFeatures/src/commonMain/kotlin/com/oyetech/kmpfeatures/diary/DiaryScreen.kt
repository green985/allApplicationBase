package com.oyetech.kmpfeatures.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.OperationState
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
    Scaffold { contentPadding ->
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
            Box(modifier = Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { onAction(DiaryAction.AddEntryClicked) },
                ) {
                    Text(StringKeys.addEntry)
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
            when (val operation = uiState.entriesOperation) {
                OperationState.Idle -> Unit
                OperationState.Loading -> CircularProgressIndicator()
                is OperationState.Error -> {
                    Text(
                        text = operation.error.message,
                        color = AppColors.textSecondary,
                    )
                    Button(
                        onClick = { onAction(DiaryAction.RetryEntriesLoadClicked) },
                    ) {
                        Text(StringKeys.retry)
                    }
                }
            }
            uiState.entryItems.forEach { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { onAction(DiaryAction.EntryEditClicked(entry.id)) },
                    onDelete = { onAction(DiaryAction.EntryDeleteClicked(entry.id)) },
                    onStart = { onAction(DiaryAction.StartEntryTimerClicked(entry.id)) },
                    onCancel = { onAction(DiaryAction.CancelEntryTimerClicked(entry.id)) },
                    onComplete = { onAction(DiaryAction.EntryCompletedClicked(entry.id)) },
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
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            EntryEditor(
                uiState = uiState,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun DiaryEntryCard(
    entry: DiaryEntryUiState,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
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
            Column {
                Button(onClick = onDelete) { Text(StringKeys.delete) }
                Text(entry.timerStatusLabel, color = AppColors.textSecondary)
                if (entry.canStartTimer) Button(onClick = onStart) { Text(StringKeys.start) }
                if (entry.canCancelTimer) Button(onClick = onCancel) { Text(StringKeys.cancelTimer) }
                if (entry.canMarkCompleted) Button(onClick = onComplete) { Text(StringKeys.alreadyDone) }
            }
        }
    }
}

@Composable
private fun EntryEditor(
    uiState: DiaryUiState,
    onAction: (DiaryAction) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
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
            uiState.areaOptionRows.forEach { rowAreas ->
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rowAreas.forEachIndexed { index, areaOption ->
                        SegmentedButton(
                            modifier = Modifier.weight(1f),
                            selected = uiState.selectedArea == areaOption.area,
                            onClick = { onAction(DiaryAction.AreaSelected(areaOption.area)) },
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
            Text(
                text = StringKeys.duration,
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.isEntryCompleted,
                    onCheckedChange = { onAction(DiaryAction.EntryCompletionChanged(it)) },
                )
                Text(StringKeys.alreadyDone)
            }
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                uiState.timerPresetOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = uiState.selectedTimerPreset == option.preset,
                        enabled = option.isEnabled,
                        onClick = { onAction(DiaryAction.TimerPresetSelected(option.preset)) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            uiState.timerPresetOptions.size
                        ),
                    ) { Text(option.label) }
                }
            }
            if (uiState.isCustomDurationVisible) {
                OutlinedTextField(
                    value = uiState.customDurationMinutes,
                    enabled = uiState.canChangeDuration,
                    onValueChange = { onAction(DiaryAction.CustomDurationChanged(it)) },
                    label = { Text(StringKeys.custom) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.hasCustomDurationError,
                    supportingText = { Text(uiState.customDurationError) },
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.text,
                onValueChange = { onAction(DiaryAction.TextChanged(it)) },
                label = { Text(StringKeys.note) },
                minLines = 3,
            )
            Text(uiState.timerLabel, color = AppColors.textSecondary)
            when (val operation = uiState.entryOperation) {
                OperationState.Idle -> Unit
                OperationState.Loading -> Unit
                is OperationState.Error -> {
                    Text(operation.error.message, color = AppColors.textSecondary)
                    Button(onClick = { onAction(DiaryAction.RetryEntrySaveClicked) }) {
                        Text(StringKeys.retry)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    enabled = uiState.canCreateOrUpdateEntry,
                    onClick = { onAction(DiaryAction.SaveEntryClicked) },
                ) {
                    Text(uiState.primaryButtonLabel)
                }
                if (uiState.canCancelEditorTimer) {
                    Button(onClick = { onAction(DiaryAction.CancelEditorTimerClicked) }) {
                        Text(StringKeys.cancelTimer)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiaryScreenPreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val entries = EntryDummyData().generate()
    ViewModuleTheme {
        DiaryScreen(
            uiState = DiaryUiState(
                selectedDate = today,
                isTodaySelected = true,
                selectedDateLabel = today.toString(),
                entries = entries,
                entryItems = DiaryOperator.renderEntryItems(entries),
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
