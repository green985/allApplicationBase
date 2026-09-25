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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.kmpmodels.ui.event.DailyPagerAction
import com.oyetech.kmpmodels.ui.state.DailyPagerUiState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock

@Composable
fun DailyPagerScreenSetup() {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<DailyPagerOperator>(
        parameters = { parametersOf(operatorScope) },
    )
    val uiState by operator.state.collectAsState()

    DailyPagerScreen(
        uiState = uiState,
        onAction = operator::dispatch,
    )
}

@Composable
fun DailyPagerScreen(
    uiState: DailyPagerUiState,
    onAction: (DailyPagerAction) -> Unit,
) {
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
            color = AppColors.textPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                enabled = uiState.canGoPrevious,
                onClick = { onAction(DailyPagerAction.PreviousDayClicked) },
            ) {
                Text("Dün")
            }
            Text(
                text = uiState.selectedDate.toTurkishDate(),
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.textPrimary,
            )
            Button(
                enabled = uiState.canGoNext,
                onClick = { onAction(DailyPagerAction.NextDayClicked) },
            ) {
                Text("Yarın")
            }
        }
        Button(onClick = { onAction(DailyPagerAction.BackClicked) }) {
            Text("Geri")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyPagerScreenPreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ViewModuleTheme {
        DailyPagerScreen(
            uiState = DailyPagerUiState(selectedDate = today),
            onAction = {},
        )
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
