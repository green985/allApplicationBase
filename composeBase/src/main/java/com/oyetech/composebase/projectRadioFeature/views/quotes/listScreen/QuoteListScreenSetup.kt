package com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.basePagingList.PagingInitialErrorView
import com.oyetech.composebase.base.basePagingList.PagingInitialLoadingView
import com.oyetech.composebase.base.basePagingList.PagingMoreError
import com.oyetech.composebase.base.basePagingList.PagingMoreLoading
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

    val state = rememberLazyListState()

    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {
            LazyColumn(state = state) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = {
                        lazyPagingItems.itemKey {
                            it.quoteId
                        }.invoke(it)
                    },
                    itemContent = { model ->
                        val quote = lazyPagingItems.get(model)
                        if (quote != null) {
                            RandomQuotesSmallView(
                                uiState = QuotesUiState(
                                    text = quote.text,
                                    author = quote.author
                                )
                            )
                        }
                    })
                item {
                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        Timber.d("LoadState.Loading")
                        PagingMoreLoading()
                    }
                }
                item {
                    if (lazyPagingItems.loadState.append is LoadState.Error) {
                        val errorMessage =
                            (lazyPagingItems.loadState.append as LoadState.Error).error.message
                        Timber.d("LoadState.Error")
                        PagingMoreError(
                            errorMessage = errorMessage ?: "",
                            onRetry = {
                                lazyPagingItems.retry()
                            }
                        )
                    }
                }
            }

            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    Timber.d("refresh LoadState.Loading")
                    PagingInitialLoadingView()
                }

                is LoadState.Error -> {
                    Timber.d("refresh LoadState.Error")
                    val errorMessage =
                        (lazyPagingItems.loadState.append as LoadState.Error).error.message
                    PagingInitialErrorView(
                        errorMessage = errorMessage ?: "",
                        onRetry = {
                            lazyPagingItems.retry()
                        }
                    )

                }

                else -> {
                    Timber.d("refresh LoadState.not loading")
                }
            }
        }

        val listLayoutInfo by remember { derivedStateOf { state.layoutInfo } }
        val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        LaunchedEffect(currentLastVisibleIndex) {
            Timber.d("currentLastVisibleIndex == $currentLastVisibleIndex")
            vm.onEvent(QuoteListUiEvent.QuoteSeen(currentLastVisibleIndex))
        }
    }
}
