package com.oyetech.kmpmodels.ui.event

sealed interface DailyPagerAction {
    data object PreviousDayClicked : DailyPagerAction
    data object NextDayClicked : DailyPagerAction
    data object BackClicked : DailyPagerAction
    data object ErrorDismissed : DailyPagerAction
}
