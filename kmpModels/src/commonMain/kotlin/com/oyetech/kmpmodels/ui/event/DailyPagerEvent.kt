package com.oyetech.kmpmodels.ui.event

sealed interface DailyPagerEvent {
    data object PreviousDayClicked : DailyPagerEvent
    data object NextDayClicked : DailyPagerEvent
    data object BackClicked : DailyPagerEvent
    data object ErrorDismissed : DailyPagerEvent
}
