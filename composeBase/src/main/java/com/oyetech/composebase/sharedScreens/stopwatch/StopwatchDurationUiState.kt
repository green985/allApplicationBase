package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

data class StopwatchDurationUiState(
    val selectedMinutes: Int = 0,
    val durations: List<Int> = listOf(1, 5, 10, 15, 20),
    val remainingMinutes: Int = 0,
    val remainingSeconds: Int = 0,
    val formattedTime: String = "00:00",
    val isTimerFinished: Boolean = false,
) : BaseUIState()

sealed class StopwatchDurationEvent : BaseEvent() {
    data class OnDurationSelected(val minutes: Int) : StopwatchDurationEvent()
}

