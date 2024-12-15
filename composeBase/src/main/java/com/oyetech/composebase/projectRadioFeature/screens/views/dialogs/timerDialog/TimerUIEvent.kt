package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog

sealed class RadioCountTimerUIEvent {
    object DismissDialog : RadioCountTimerUIEvent()
    data class UpdateSliderPosition(val position: Float) : RadioCountTimerUIEvent()
    object TimerApply : RadioCountTimerUIEvent()
    object TimerClear : RadioCountTimerUIEvent()
    object UserInteract : RadioCountTimerUIEvent()
}

data class RadioCountTimerUIState(
    val isUserInteract: Boolean = false,
    val isDialogVisible: Boolean = false,
    val sliderPosition: Float = 0f,
    val sliderTimeText: String = "0 min",
)