package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import kotlinx.coroutines.flow.MutableStateFlow

interface ContentOperationViewModelSlice {

    fun onContentEvent(event: ContentOperationEvent)

    fun getContentOperationUiState(
        contentId: String,
        updateLoading: (Boolean) -> Unit,
        updateErrorText: (String) -> Unit,
    ): MutableStateFlow<ContentOperationUiState>
}