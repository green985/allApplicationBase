package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

data class StopwatchUiState(
    val formattedTime: String = "00:00",
) : BaseUIState()

sealed class StopwatchEvent : BaseEvent() {
    data object OnCancelClicked : StopwatchEvent()
}

