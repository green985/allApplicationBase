package com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.ListCustomLoadingView
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.QuotesVM
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.mapToUiState
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-19.12.2024-
-18:41-
 **/

class QuotePagingSource(
    private val apiService: QuoteDataOperationRepository,
    private val getOldDataList: () -> Array<String>,
) : PagingSource<Int, QuotesUiState>() {
    override fun getRefreshKey(state: PagingState<Int, QuotesUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuotesUiState> {
        return try {
            withContext(Dispatchers.IO) {
                val page = params.key ?: 1
                Timber.d("getRefreshKey load == " + page)

                val response =
                    apiService.getQuoteUnseenFlow(getOldDataList.invoke()).mapToUiState()
                        .firstOrNull()
                        ?: emptyList()
                LoadResult.Page(
                    data = response,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.isEmpty()) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}

@Composable
fun QuoteListScreenSetup() {
    val vm = koinViewModel<QuotesVM>()
    val complexItemListState by vm.complexItemViewState.collectAsStateWithLifecycle()

    val pagerState = vm.getQuotesPager().collectAsLazyPagingItems()
    val lazyPagingItems = vm.getQuotesPager().collectAsLazyPagingItems()

    val state = rememberLazyListState()

    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {

            when (lazyPagingItems.loadState.append) {
                is LoadState.Error -> {
                    Timber.d("append LoadState.Error")

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Black),
                        text = "append Bir hata oluştu.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is LoadState.Loading -> {
                    Timber.d("LoadState.Loading")
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Black)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                else -> {
                    Timber.d("append LoadState.else ")
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
                    }
                }
            }




            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    Timber.d("refresh LoadState.Loading")
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Red)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                is LoadState.Error -> {
                    Timber.d("refresh LoadState.Error")

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Red),
                        text = "Bir hata oluştu.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    Timber.d("refresh LoadState.not loading")

                    val lastVisibleItemIndex =
                        state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                }
            }

//
//            QuoteListScreen(
//                complexItemViewState = complexItemListState,
//                pagerState = pagerState,
//                eventHandle = { event: QuoteListUiEvent ->
//                    vm.onEvent(event = event)
//                },
//                radioListEventHandle = { event: ListUIEvent ->
//                    vm.handleListEvent(event)
//                }
//            )
        }
    }
//
//    val lazyListState = state

//    val listLayoutInfo by remember { derivedStateOf { state.layoutInfo } }
//    val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//
//
//    rememberCoroutineScope().launch {
//        snapshotFlow { state.layoutInfo.visibleItemsInfo }
//            .map { it.firstOrNull() }
//            .distinctUntilChanged()
//            .collect {
//                it?.index?.let { it1 -> vm.itemVisible(it1) }
//
//            }
//    }
}

@Composable
fun QuoteListScreen(
    complexItemViewState: ComplexItemListState<QuotesUiState>,
    eventHandle: (QuoteListUiEvent) -> Unit,
    radioListEventHandle: (ListUIEvent) -> Unit = {},
    pagerState: LazyPagingItems<QuotesUiState>,
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