package com.oyetech.kmpmodels.ui.event

sealed interface DiaryEvent {
    data object PreviousDayClicked : DiaryEvent
    data object NextDayClicked : DiaryEvent
    data class QuoteChanged(val value: String) : DiaryEvent
    data class AreaChanged(val value: String) : DiaryEvent
    data class TextChanged(val value: String) : DiaryEvent
    data object AddEntryClicked : DiaryEvent
    data object SaveEntryClicked : DiaryEvent
    data object BackClicked : DiaryEvent
    data object ErrorDismissed : DiaryEvent
}
