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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun DiaryScreenSetup(
    onBackClick: () -> Unit,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var dayOffset by remember { mutableStateOf(0) }
    val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + dayOffset)
    val entries = remember {
        mutableStateListOf(
            EntryEntity("1", "Zihin", "Bugün için küçük bir başlangıç yaptım."),
            EntryEntity("2", "Beden", "Kısa bir yürüyüş iyi geldi."),
        )
    }
    var isEditorVisible by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf<AreaEntry?>(null) }
    var text by remember { mutableStateOf("") }
    var dayQuote by remember { mutableStateOf("") }

    fun saveEntry() {
        val area = selectedArea
        if (area != null && text.isNotBlank()) {
            entries.add(EntryEntity(entries.size.toString(), area.name, text.trim()))
            selectedArea = null
            text = ""
            isEditorVisible = false
        }
    }

    DiaryScreen(
        uiState = DiaryUiState(
            selectedDate = selectedDate,
            isTodaySelected = selectedDate == today,
            entries = entries.toList(),
            isEditorVisible = isEditorVisible,
            selectedArea = selectedArea,
            text = text,
            dayQuote = dayQuote,
        ),
        onAction = { action ->
            when (action) {
                DiaryAction.PreviousDayClicked -> dayOffset--
                DiaryAction.NextDayClicked -> dayOffset++
                is DiaryAction.QuoteChanged -> dayQuote = action.value
                is DiaryAction.AreaSelected -> selectedArea = action.value
                is DiaryAction.TextChanged -> text = action.value
                DiaryAction.AddEntryClicked -> isEditorVisible = true
                DiaryAction.EntryDialogDismissed -> {
                    if (selectedArea == null && text.isBlank()) {
                        isEditorVisible = false
                    } else {
                        saveEntry()
                    }
                }

                DiaryAction.SaveEntryClicked -> saveEntry()

                DiaryAction.BackClicked -> onBackClick()
                DiaryAction.ErrorDismissed -> Unit
            }
        },
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
                Text("+")
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
                text = "Günlük",
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
                    Text("Dün")
                }
                Column {
                    Text(
                        text = uiState.selectedDate.toTurkishDate(),
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.textPrimary,
                    )
                    Text(
                        text = if (uiState.isTodaySelected) "Bugün" else "Seçili gün",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.textSecondary,
                    )
                }
                Button(
                    enabled = uiState.canGoNext,
                    onClick = { onAction(DiaryAction.NextDayClicked) },
                ) {
                    Text("Yarın")
                }
            }
            if (uiState.isTodaySelected) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.dayQuote,
                    onValueChange = { onAction(DiaryAction.QuoteChanged(it)) },
                    label = { Text("Bugünün sözü") },
                    placeholder = { Text("Bugün için bir söz yaz") },
                    singleLine = true,
                )
            }
            uiState.entries.forEach { entry ->
                DiaryEntryCard(entry)
            }
            Button(onClick = { onAction(DiaryAction.BackClicked) }) {
                Text("Geri")
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
                        "Zihin",
                        "Bugün için küçük bir başlangıç yaptım."
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
                text = "Yeni kayıt",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.textPrimary,
            )
            Text(
                text = "Alan",
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
                label = { Text("Not") },
                minLines = 3,
            )
            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    enabled = selectedArea != null && text.isNotBlank(),
                    onClick = onSave,
                ) {
                    Text("Save")
                }
            }
        }
    }
}

private fun LocalDate.toTurkishDate(): String {
    val months = arrayOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık",
    )
    return "$day ${months[monthNumber - 1]} $year"
}

private fun AreaEntry.displayName(): String = when (this) {
    AreaEntry.WORK -> "İş / İnşa"
    AreaEntry.BODY -> "Beden"
    AreaEntry.HEALTH -> "Sağlık"
    AreaEntry.MIND -> "Zihin"
    AreaEntry.CHARACTER -> "Karakter"
    AreaEntry.PEOPLE -> "İnsanlar"
    AreaEntry.LIFE -> "Hayat"
}
