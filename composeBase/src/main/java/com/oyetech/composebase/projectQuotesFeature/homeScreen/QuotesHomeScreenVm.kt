package com.oyetech.composebase.projectQuotesFeature.homeScreen

import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarActionItems
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.models.quotes.enums.QuoteListEnum
import com.oyetech.tools.contextHelper.getAppName
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-6.01.2025-
-23:02-
 **/

class QuotesHomeScreenVm(appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers) :
    BaseViewModel(appDispatchers) {

    val radioToolbarState: MutableStateFlow<QuoteToolbarState> =
        MutableStateFlow(
            QuoteToolbarState(
                context.getAppName(), actionButtonState = persistentListOf(
                    QuoteToolbarActionItems.Search(
                        R.drawable.ic_search
                    )
                )
            )
        )
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