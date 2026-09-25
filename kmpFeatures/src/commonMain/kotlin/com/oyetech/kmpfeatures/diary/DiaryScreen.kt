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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class DiaryEntry(
    val area: String,
    val text: String,
)

@Composable
fun DiaryScreenSetup(
    onBackClick: () -> Unit,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var dayOffset by remember { mutableStateOf(0) }
    val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + dayOffset)
    val entries = remember {
        mutableStateListOf(
            DiaryEntry("Zihin", "Bugün için küçük bir başlangıç yaptım."),
            DiaryEntry("Beden", "Kısa bir yürüyüş iyi geldi."),
        )
    }
    var isEditorVisible by remember { mutableStateOf(false) }
    var area by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var dayQuote by remember { mutableStateOf("") }

    DiaryScreen(
        uiState = DiaryUiState(
            selectedDate = selectedDate,
            isTodaySelected = selectedDate == today,
            entries = entries.toList(),
            isEditorVisible = isEditorVisible,
            area = area,
            text = text,
            dayQuote = dayQuote,
        ),
        onEvent = { event ->
            when (event) {
                DiaryEvent.PreviousDayClicked -> dayOffset--
                DiaryEvent.NextDayClicked -> dayOffset++
                is DiaryEvent.QuoteChanged -> dayQuote = event.value
                is DiaryEvent.AreaChanged -> area = event.value
                is DiaryEvent.TextChanged -> text = event.value
                DiaryEvent.AddEntryClicked -> isEditorVisible = true
                DiaryEvent.SaveEntryClicked -> {
                    if (area.isNotBlank() && text.isNotBlank()) {
                        entries.add(DiaryEntry(area.trim(), text.trim()))
                        area = ""
                        text = ""
                        isEditorVisible = false
                    }
                }

                DiaryEvent.BackClicked -> onBackClick()
                DiaryEvent.ErrorDismissed -> Unit
            }
        },
    )
}

data class DiaryUiState(
    val selectedDate: LocalDate,
    val isTodaySelected: Boolean,
    val entries: List<DiaryEntry>,
    val isEditorVisible: Boolean,
    val area: String,
    val text: String,
    val dayQuote: String,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)

sealed interface DiaryEvent {
    data object PreviousDayClicked : DiaryEvent
    data object NextDayClicked : DiaryEvent
    data class QuoteChanged(val value: String) : DiaryEvent
    data class AreaChanged(val value: String) : DiaryEvent
    data class TextChanged(val value: String) : DiaryEvent
    data object AddEntryClicked : DiaryEvent
    data object SaveEntryClicked : DiaryEvent
    data object BackClicked : DiaryEvent
    data object ErrorDismissed : DiaryEvent
}

@Composable
fun DiaryScreen(
    uiState: DiaryUiState,
    onEvent: (DiaryEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                onClick = { onEvent(DiaryEvent.PreviousDayClicked) },
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
                onClick = { onEvent(DiaryEvent.NextDayClicked) },
            ) {
                Text("Yarın")
            }
        }
        if (uiState.isTodaySelected) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.dayQuote,
                onValueChange = { onEvent(DiaryEvent.QuoteChanged(it)) },
                label = { Text("Bugünün sözü") },
                placeholder = { Text("Bugün için bir söz yaz") },
                singleLine = true,
            )
        }
        uiState.entries.forEach { entry ->
            DiaryEntryCard(entry)
        }
        if (uiState.isEditorVisible) {
            EntryEditor(
                area = uiState.area,
                text = uiState.text,
                onAreaChange = { onEvent(DiaryEvent.AreaChanged(it)) },
                onTextChange = { onEvent(DiaryEvent.TextChanged(it)) },
                onSave = { onEvent(DiaryEvent.SaveEntryClicked) },
            )
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(DiaryEvent.AddEntryClicked) },
            ) {
                Text("+")
            }
        }
        Button(onClick = { onEvent(DiaryEvent.BackClicked) }) {
            Text("Geri")
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
                entries = listOf(DiaryEntry("Zihin", "Bugün için küçük bir başlangıç yaptım.")),
                isEditorVisible = false,
                area = "",
                text = "",
                dayQuote = "",
            ),
            onEvent = {},
        )
    }
}

@Composable
private fun DiaryEntryCard(entry: DiaryEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.area,
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
    area: String,
    text: String,
    onAreaChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Yeni kayıt",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.textPrimary,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = area,
                onValueChange = onAreaChange,
                label = { Text("Area") },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onTextChange,
                label = { Text("Not") },
                minLines = 3,
            )
            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    enabled = area.isNotBlank() && text.isNotBlank(),
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
