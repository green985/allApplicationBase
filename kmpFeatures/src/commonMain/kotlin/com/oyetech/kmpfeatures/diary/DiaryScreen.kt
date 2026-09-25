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
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private data class DiaryEntry(
    val area: String,
    val text: String,
)

@Composable
fun DiaryScreen(
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
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                enabled = dayOffset > -1,
                onClick = { dayOffset-- },
            ) {
                Text("Dün")
            }
            Column {
                Text(
                    text = selectedDate.toTurkishDate(),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = if (dayOffset == 0) "Bugün" else "Seçili gün",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Button(
                enabled = dayOffset < 1,
                onClick = { dayOffset++ },
            ) {
                Text("Yarın")
            }
        }
        if (selectedDate == today) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dayQuote,
                onValueChange = { dayQuote = it },
                label = { Text("Bugünün sözü") },
                placeholder = { Text("Bugün için bir söz yaz") },
                singleLine = true,
            )
        }
        entries.forEach { entry ->
            DiaryEntryCard(entry)
        }
        if (isEditorVisible) {
            EntryEditor(
                area = area,
                text = text,
                onAreaChange = { area = it },
                onTextChange = { text = it },
                onSave = {
                    if (area.isNotBlank() && text.isNotBlank()) {
                        entries.add(DiaryEntry(area.trim(), text.trim()))
                        area = ""
                        text = ""
                        isEditorVisible = false
                    }
                },
            )
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isEditorVisible = true },
            ) {
                Text("+")
            }
        }
        Button(onClick = onBackClick) {
            Text("Geri")
        }
    }
}

@Composable
private fun DiaryEntryCard(entry: DiaryEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.area,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyLarge,
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
