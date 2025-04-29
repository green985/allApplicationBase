package com.oyetech.composebase.projectQuotesFeature.searchScreen

import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-15.02.2025-
-22:16-
 **/

data class QuoteSearchUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = " ",
    val searchQuery: String = "",
    val searchResults: ImmutableList<QuoteUiState> = persistentListOf(),
    val expanded: Boolean = false,
)

sealed class QuoteSearchEvent {

    data class SearchQueryChanged(val query: String) : QuoteSearchEvent()
    data class ExpandedChange(val expanded: Boolean) : QuoteSearchEvent()
}