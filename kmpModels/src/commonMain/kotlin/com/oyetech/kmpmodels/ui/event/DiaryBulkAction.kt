package com.oyetech.kmpmodels.ui.event

sealed interface DiaryBulkAction {
    data class JsonChanged(val value: String) : DiaryBulkAction
    data object PreviewClicked : DiaryBulkAction
    data object SubmitClicked : DiaryBulkAction
    data object RetryTagsClicked : DiaryBulkAction
    data object BackClicked : DiaryBulkAction
}
