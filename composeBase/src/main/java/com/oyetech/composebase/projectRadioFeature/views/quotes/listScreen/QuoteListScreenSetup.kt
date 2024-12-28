package com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.basePagingList.BasePagingListScreen
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.QuotesVM
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-19.12.2024-
-18:41-
 **/

@Composable
fun QuoteListScreenSetup() {
    val vm = koinViewModel<QuotesVM>()
    val lazyPagingItems = vm.quotesPage.collectAsLazyPagingItems()

    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { quote -> quote.quoteId },
                onBindItem = { quote ->
                    RandomQuotesSmallView(
                        uiState = QuotesUiState(
                            text = quote.text,
                            author = quote.author
                        )
                    )
                },
                onItemVisible = { currentLastVisibleIndex ->
                    Timber.d("currentLastVisibleIndex == $currentLastVisibleIndex")
                    vm.onEvent(QuoteListUiEvent.QuoteSeen(currentLastVisibleIndex))
                },
            )
        }
    }
}
