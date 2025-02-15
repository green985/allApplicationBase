package com.oyetech.composebase.projectQuotesFeature.searchScreen

/**
Created by Erdi Özbek
-15.02.2025-
-22:16-
 **/

data class QuoteSearchUiState(val isLoading: Boolean = false)

sealed class QuoteSearchEvent {
    data class Idle(val data: Int) : QuoteSearchEvent()
    object Idlee : QuoteSearchEvent()
}