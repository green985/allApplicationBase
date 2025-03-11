package com.oyetech.composebase.projectQuotesFeature.quotes.listScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.QuotesVM
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteListUiEvent.QuoteSeen
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-19.12.2024-
-18:41-
 **/

@Composable
fun QuoteListScreenSetup(navigationRoute: (navigationRoute: String) -> Unit) {
    val vm = koinViewModel<QuotesVM>()
    val lazyPagingItems = vm.quotesPage.collectAsLazyPagingItems()

    val contentOperationVm = vm.contentOperationVm

    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { quote -> quote.quoteId },
                onBindItem = { quote ->
                    val contentOperationState by contentOperationVm.getContentStateFlow(quote.quoteId)
                        .collectAsStateWithLifecycle()
                    RandomQuotesSmallView(
                        modifier = Modifier.clickable {
                            navigationRoute(
                                QuoteAppProjectRoutes.QuoteDetailRoute.withArgs(
                                    ScreenKey.quoteId to quote.quoteId,
                                )
                            )
                        },
                        uiState = quote, navigationRoute = navigationRoute,
                        contentOperationUiState = contentOperationState,
                        contentOperationEvent = { contentOperationVm.onContentEvent(it) },
                        contentOperationActive = true,
                    )
                },
                onItemVisible = { currentLastVisibleIndex ->
//                    Timber.d("currentLastVisibleIndex == $currentLastVisibleIndex")
                    vm.onEvent(QuoteSeen(currentLastVisibleIndex))
                },
            )
        }
    }
}
