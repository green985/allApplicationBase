package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import kotlinx.coroutines.flow.MutableStateFlow

interface ContentOperationViewModelSlice {

    context(BaseViewModel)
    fun onContentEvent(event: ContentOperationEvent)

    fun initContentOperationState(
        quoteId: String,
        contentOperationUiState: MutableStateFlow<ContentOperationUiState>,
        uiState: MutableStateFlow<QuoteUiState>,
    )
}