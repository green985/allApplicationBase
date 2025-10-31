package com.oyetech.composebase.helpers.viewProperties

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import timber.log.Timber

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit,
) {
    // will handle initial loading and error states...
//    InfiniteListHandler(listState) {
//        listUiState.onLoadMore?.invoke()
//    }

    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            val pair = Pair(lastVisibleItemIndex, totalItemsNumber - buffer)
            Timber.d("InfiniteListHandler: $pair")
            Timber.d("InfiniteListHandler : $totalItemsNumber")
            pair
        }
    }


    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .filter { it.second > 0 }
            .filter { it.first > it.second }
            .distinctUntilChanged()
            .collect {
                if (listState.layoutInfo.totalItemsCount == 0) {
                    Timber.d("InfiniteListHandler: No items in the list, skipping load more")
                    return@collect
                }
                Timber.d("InfiniteListHandler: Triggering onLoadMore ")
                onLoadMore()
            }
    }
}