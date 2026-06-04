package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

data class StopwatchDurationUiState(
    val selectedMinutes: Int = 0,
) : BaseUIState()

sealed class StopwatchDurationEvent : BaseEvent() {
    data class OnDurationSelected(val minutes: Int) : StopwatchDurationEvent()
}

