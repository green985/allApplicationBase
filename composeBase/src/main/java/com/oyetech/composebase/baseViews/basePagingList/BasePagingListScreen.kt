package com.oyetech.composebase.baseViews.basePagingList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.PagingMoreError
import com.oyetech.composebase.baseViews.loadingErrors.PagingMoreLoading

@Composable
fun <T : Any> BasePagingListScreen(
    items: LazyPagingItems<T>,
    itemKey: (T) -> Any,
    onBindItem: @Composable (T) -> Unit,
    onItemVisible: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
) {
    val state = rememberLazyListState()

    Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    )
    {

        when (items.loadState.refresh) {
            is LoadState.Loading -> {
                LoadingScreenFullSize(modifier)

            }

            is LoadState.Error -> {
                val errorMessage =
                    (items.loadState.refresh as LoadState.Error).error.message
                ErrorScreenFullSize(
                    modifier = modifier,
                    errorMessage = errorMessage ?: "",
                    onRetry = { items.retry() }
                )
            }

            is LoadState.NotLoading -> {
                if (items.itemCount == 0) {
                    ErrorScreenFullSize(
                        modifier = modifier,
                        errorMessage = "No data found",
                        onRetry = { items.retry() }
                    )
                }

            }

            else -> Unit
        }

        LazyColumn(modifier = modifier, state = state, reverseLayout = reverseLayout) {
            items(
                count = items.itemCount,
                key = { index ->
                    items.itemKey { item -> itemKey(item) }.invoke(index)
                },
                itemContent = { index ->
                    val item = items[index]
                    if (item != null) {
                        onBindItem(item)
                    }
                }
            )
            item {
                if (items.loadState.append is LoadState.Loading) {
                    PagingMoreLoading()
                }
            }
            item {
                if (items.loadState.append is LoadState.Error) {
                    val errorMessage =
                        (items.loadState.append as LoadState.Error).error.message
                    PagingMoreError(
                        errorMessage = errorMessage ?: "",
                        onRetry = { items.retry() },
                    )
                }
            }
        }

    }

    // Trigger event when last visible item index changes
    val listLayoutInfo by remember { derivedStateOf { state.layoutInfo } }
    val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    LaunchedEffect(currentLastVisibleIndex) {
        onItemVisible(currentLastVisibleIndex)
    }

}
