package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

/**
Created by Erdi Özbek
-3.02.2025-
-22:29-
 **/

data class AdviceQuoteDebugUiState(val isLoading: Boolean = false)

sealed class AdviceQuoteDebugEvent {
    data class Idle(val data: Int) : AdviceQuoteDebugEvent()
    object Idlee : AdviceQuoteDebugEvent()
}