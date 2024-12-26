package com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.ListCustomLoadingView
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.QuotesVM
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-19.12.2024-
-18:41-
 **/

@Composable
fun QuoteListScreenSetup() {
    val vm = koinViewModel<QuotesVM>()
    val complexItemListState by vm.complexItemViewState.collectAsStateWithLifecycle()

    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {

            QuoteListScreen(
                complexItemViewState = complexItemListState,
                eventHandle = { event: QuoteListUiEvent ->
                    vm.onEvent(event = event)
                },
                radioListEventHandle = { event: ListUIEvent ->
                    vm.handleListEvent(event)
                }
            )
        }
    }
}

@Composable
fun QuoteListScreen(
    complexItemViewState: ComplexItemListState<QuotesUiState>,
    eventHandle: (QuoteListUiEvent) -> Unit,
    radioListEventHandle: (ListUIEvent) -> Unit = {},
) {

    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            radioListEventHandle(ListUIEvent.LoadMore)
        },
    )

    LoadableLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        isRefreshing = complexItemViewState.isRefreshing,
        isLoadingInitial = complexItemViewState.isLoadingInitial,
        isLoadingMore = complexItemViewState.isLoadingMore,
        isErrorInitial = complexItemViewState.isErrorInitial,
        isErrorMore = complexItemViewState.isErrorMore,
        onRetry = { radioListEventHandle(ListUIEvent.Retry) },
        isEmptyList = complexItemViewState.isEmptyList,
        onRefresh = { radioListEventHandle(ListUIEvent.Refresh) },
        errorMessage = complexItemViewState.errorMessage,
        onItemVisible = { radioListEventHandle.invoke(ListUIEvent.ItemVisible(it)) },
        loadMoreLoadingContent = { ListCustomLoadingView() },
        content = {
            items(items = complexItemViewState.items, key = { it.quoteId }, itemContent = { quote ->
                RandomQuotesSmallView(uiState = quote)
            })
        },
    )

}