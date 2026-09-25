package com.oyetech.kmpmodels.ui.event

import com.oyetech.kmpmodels.entity.AreaEntry

sealed interface DiaryAction {
    data object PreviousDayClicked : DiaryAction
    data object NextDayClicked : DiaryAction
    data class QuoteChanged(val value: String) : DiaryAction
    data class AreaSelected(val value: AreaEntry) : DiaryAction
    data class TextChanged(val value: String) : DiaryAction
    data class EntryEditClicked(val entryId: String) : DiaryAction
    data object AddEntryClicked : DiaryAction
    data object EntryDialogDismissed : DiaryAction
    data object SaveEntryClicked : DiaryAction
    data object BackClicked : DiaryAction
    data object ErrorDismissed : DiaryAction
}
