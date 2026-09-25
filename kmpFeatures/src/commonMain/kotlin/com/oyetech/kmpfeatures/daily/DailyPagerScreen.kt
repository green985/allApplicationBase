package com.oyetech.kmpfeatures.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun DailyPagerScreen(
    onBackClick: () -> Unit,
) {
    var dayOffset by remember { mutableIntStateOf(0) }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + dayOffset)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
    ) {
        Text(
            text = "Günlük içerik",
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                enabled = dayOffset > -1,
                onClick = { dayOffset-- },
            ) {
                Text("Dün")
            }
            Text(
                text = selectedDate.toTurkishDate(),
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                enabled = dayOffset < 1,
                onClick = { dayOffset++ },
            ) {
                Text("Yarın")
            }
        }
        Button(onClick = onBackClick) {
            Text("Geri")
        }
    }
}

private fun kotlinx.datetime.LocalDate.toTurkishDate(): String {
    val months = arrayOf(
        "Ocak",
        "Şubat",
        "Mart",
        "Nisan",
        "Mayıs",
        "Haziran",
        "Temmuz",
        "Ağustos",
        "Eylül",
        "Ekim",
        "Kasım",
        "Aralık",
    )
    return "$day ${months[monthNumber - 1]} $year"
}
