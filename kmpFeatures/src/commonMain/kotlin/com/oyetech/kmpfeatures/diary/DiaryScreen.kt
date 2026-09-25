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
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.datetime.LocalDate
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
                        text = uiState.selectedDate.toTurkishDate(),
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
            if (uiState.isTodaySelected) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.dayQuote,
                    onValueChange = { onAction(DiaryAction.QuoteChanged(it)) },
                    label = { Text(StringKeys.todaysQuote) },
                    placeholder = { Text(StringKeys.writeTodaysQuote) },
                    singleLine = true,
                )
            }
            uiState.entries.forEach { entry ->
                DiaryEntryCard(entry)
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
                onAreaSelected = { onAction(DiaryAction.AreaSelected(it)) },
                onTextChange = { onAction(DiaryAction.TextChanged(it)) },
                onSave = { onAction(DiaryAction.SaveEntryClicked) },
            )
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
                entries = listOf(
                    EntryEntity(
                        "1",
                        StringKeys.mindArea,
                        StringKeys.sampleMindEntry
                    )
                ),
                isEditorVisible = true,
                selectedArea = null,
                text = "",
                dayQuote = "",
            ),
            onAction = {},
        )
    }
}

@Composable
private fun DiaryEntryCard(entry: EntryEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.areaId,
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary,
            )
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textPrimary,
            )
        }
    }
}

@Composable
private fun EntryEditor(
    selectedArea: AreaEntry?,
    text: String,
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
            AreaEntry.entries.toList().chunked(4).forEach { rowAreas ->
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rowAreas.forEachIndexed { index, area ->
                        SegmentedButton(
                            modifier = Modifier.weight(1f),
                            selected = selectedArea == area,
                            onClick = { onAreaSelected(area) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = rowAreas.size,
                            ),
                        ) {
                            Text(area.displayName())
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

private fun LocalDate.toTurkishDate(): String {
    return "$day ${StringKeys.turkishMonths[monthNumber - 1]} $year"
}

private fun AreaEntry.displayName(): String = when (this) {
    AreaEntry.WORK -> StringKeys.workArea
    AreaEntry.BODY -> StringKeys.bodyArea
    AreaEntry.HEALTH -> StringKeys.healthArea
    AreaEntry.MIND -> StringKeys.mindArea
    AreaEntry.CHARACTER -> StringKeys.characterArea
    AreaEntry.PEOPLE -> StringKeys.peopleArea
    AreaEntry.LIFE -> StringKeys.lifeArea
}
