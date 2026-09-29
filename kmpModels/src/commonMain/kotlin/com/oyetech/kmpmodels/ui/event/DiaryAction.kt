package com.oyetech.kmpmodels.ui.event

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryTimerPreset

sealed interface DiaryAction {
    data object PreviousDayClicked : DiaryAction
    data object NextDayClicked : DiaryAction
    data class QuoteChanged(val value: String) : DiaryAction
    data class AreaSelected(val value: AreaEntry) : DiaryAction
    data class TextChanged(val value: String) : DiaryAction
    data class EntryEditClicked(val entryId: String) : DiaryAction
    data class EntryDeleteClicked(val entryId: String) : DiaryAction
    data class TimerPresetSelected(val preset: EntryTimerPreset) : DiaryAction
    data class CustomDurationChanged(val minutes: String) : DiaryAction
    data object SaveEntryWithoutClosingClicked : DiaryAction
    data class StartEntryTimerClicked(val entryId: String) : DiaryAction
    data class CancelEntryTimerClicked(val entryId: String) : DiaryAction
    data class EntryCompletedClicked(val entryId: String) : DiaryAction
    data object AddEntryClicked : DiaryAction
    data object EntryDialogDismissed : DiaryAction
    data object SaveEntryClicked : DiaryAction
    data object RetryQuoteSaveClicked : DiaryAction
    data object RetryEntrySaveClicked : DiaryAction
    data object RetryEntriesLoadClicked : DiaryAction
    data object BackClicked : DiaryAction
    data object ErrorDismissed : DiaryAction
}
