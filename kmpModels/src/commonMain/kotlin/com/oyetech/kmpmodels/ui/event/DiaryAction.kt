package com.oyetech.kmpmodels.ui.event

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryTimerPreset

sealed interface DiaryAction {
    data object PreviousDayClicked : DiaryAction
    data object NextDayClicked : DiaryAction
    data object TodayClicked : DiaryAction
    data object AreaMenuClicked : DiaryAction
    data object AreaMenuDismissed : DiaryAction
    data class AreaFilterSelected(val area: AreaEntry) : DiaryAction
    data class ListTagFilterSelected(val tagId: String?) : DiaryAction
    data object RetryListTagsLoadClicked : DiaryAction
    data class QuoteChanged(val value: String) : DiaryAction
    data class AreaSelected(val value: AreaEntry) : DiaryAction
    data class TagQueryChanged(val value: String) : DiaryAction
    data class TagSelected(val tagId: String) : DiaryAction
    data object TagCleared : DiaryAction
    data object CreateTagClicked : DiaryAction
    data object RetryTagsLoadClicked : DiaryAction
    data class TextChanged(val value: String) : DiaryAction
    data class EntryEditClicked(val entryId: String) : DiaryAction
    data class EntryDeleteClicked(val entryId: String) : DiaryAction
    data class TimerPresetSelected(val preset: EntryTimerPreset) : DiaryAction
    data class CustomDurationChanged(val minutes: String) : DiaryAction
    data class EntryCompletionChanged(val checked: Boolean) : DiaryAction
    data object SaveEntryWithoutClosingClicked : DiaryAction
    data class StartEntryTimerClicked(val entryId: String) : DiaryAction
    data class CancelEntryTimerClicked(val entryId: String) : DiaryAction
    data object CancelEditorTimerClicked : DiaryAction
    data class EntryCompletedClicked(val entryId: String) : DiaryAction
    data object AddEntryClicked : DiaryAction
    data object ExportDiaryHtmlClicked : DiaryAction
    data object ExportDiaryArchiveClicked : DiaryAction
    data object EntryDialogDismissed : DiaryAction
    data object EntryDialogCancelled : DiaryAction
    data object SaveEntryClicked : DiaryAction
    data object RetryQuoteSaveClicked : DiaryAction
    data object RetryEntrySaveClicked : DiaryAction
    data object RetryEntriesLoadClicked : DiaryAction
    data object BackClicked : DiaryAction
    data object ErrorDismissed : DiaryAction
}
