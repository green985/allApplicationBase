package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.domain.repository.stopwatch.StopwatchSession

data class StopwatchDurationUiState(
    val durations: List<StopwatchDurationItem> = emptyList(),
    val isTimerFinished: Boolean = false,
) : BaseUIState()

data class StopwatchDurationItem(
    val session: StopwatchSession,
    val label: String,
)

sealed class StopwatchDurationEvent : BaseEvent() {
    data class OnDurationSelected(val session: StopwatchSession) : StopwatchDurationEvent()
}

