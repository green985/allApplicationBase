package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.domain.repository.stopwatch.StopwatchTag

data class StopwatchUiState(
    val formattedTime: String = "00:00",
    val suggestedTags: List<StopwatchTag> = emptyList(),
    val selectedTag: StopwatchTag? = null,
) : BaseUIState()

sealed class StopwatchEvent : BaseEvent() {
    data object OnCancelClicked : StopwatchEvent()
    data class OnTagSelected(val tag: StopwatchTag) : StopwatchEvent()
}

