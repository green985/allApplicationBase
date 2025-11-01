package com.oyetech.composebase.helpers.viewProperties

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import timber.log.Timber

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    isLoading: Boolean,
    buffer: Int = 2,
    onLoadMore: () -> Unit,
) {
    val lastTriggeredTotalCount = remember { mutableIntStateOf(-1) }

    val shouldLoadMoreTriple = remember {
        derivedStateOf {
            val layout = listState.layoutInfo
            val total = layout.totalItemsCount
            val lastIndex = layout.visibleItemsInfo.lastOrNull()?.index ?: -1
            val thresholdIndex = (total - 1 - buffer).coerceAtLeast(0)
            Triple(lastIndex, thresholdIndex, total)
        }
    }

    LaunchedEffect(shouldLoadMoreTriple) {
        snapshotFlow { shouldLoadMoreTriple.value }
            .filter { !isLoading }
            .filter { (lastIndex, thresholdIndex, total) ->
                total > 0 && lastIndex >= thresholdIndex
            }
            .distinctUntilChanged()
            .collect { (_, _, total) ->
                if (lastTriggeredTotalCount.intValue != total) {
                    lastTriggeredTotalCount.intValue = total
                    Timber.d("InfiniteListHandler: Triggering onLoadMore for total: $total")
                    onLoadMore()
                }
            }
    }
}