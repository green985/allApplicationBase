package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import kotlinx.coroutines.flow.MutableStateFlow

interface ContentOperationViewModelSlice {
    fun getContentOperationUiState(contentId: String): MutableStateFlow<ContentOperationUiState>

    context(BaseViewModel)
    fun onContentEvent(event: ContentOperationEvent)
}