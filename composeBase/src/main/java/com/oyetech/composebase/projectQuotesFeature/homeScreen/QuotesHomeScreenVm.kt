package com.oyetech.composebase.projectQuotesFeature.homeScreen

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.quotes.enums.QuoteListEnum
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-6.01.2025-
-23:02-
 **/

class QuotesHomeScreenVm(appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers) :
    BaseViewModel(appDispatchers) {

    val radioToolbarState: MutableStateFlow<QuoteToolbarState> =
        MutableStateFlow(QuoteToolbarState(LanguageKey.adviceQuote))
    val uiState: MutableStateFlow<QuotesHomeState> = MutableStateFlow(QuotesHomeState())

    fun onEvent(event: QuotesHomeEvent) {
        when (event) {
//            is QuotesHomeEvent.OnQuoteClicked -> {
//
//            }
        }
    }

    fun handleToolbarAction(it: QuoteToolbarEvent, quoteListEnum: QuoteListEnum) {

    }
}