package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.domain.repository.stopwatch.StopwatchSession

data class StopwatchDurationUiState(
    val selectedDurationSeconds: Int = 0,
    val durations: List<StopwatchDurationItem> = emptyList(),
    val remainingMinutes: Int = 0,
    val remainingSeconds: Int = 0,
    val formattedTime: String = "00:00",
    val isTimerFinished: Boolean = false,
) : BaseUIState()

data class StopwatchDurationItem(
    val session: StopwatchSession,
    val label: String,
)

sealed class StopwatchDurationEvent : BaseEvent() {
    data class OnDurationSelected(val session: StopwatchSession) : StopwatchDurationEvent()
}

