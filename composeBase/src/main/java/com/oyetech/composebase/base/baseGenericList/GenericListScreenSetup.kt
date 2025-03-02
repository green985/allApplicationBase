package com.oyetech.composebase.base.baseGenericList

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState

@Composable
fun <T> GenericListScreenSetup(
    modifier: Modifier = Modifier,
    listViewState: GenericListState<T>,
    content: LazyListScope.() -> Unit,
) {
    LoadableLazyColumn(
        state = rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() }),
        isRefreshing = listViewState.isRefreshing,
        isLoadingInitial = listViewState.isLoadingInitial,
        isEmptyList = listViewState.items.isEmpty(),
        errorMessage = listViewState.errorMessage,
        onRefresh = { listViewState.triggerRefresh?.invoke() },
        modifier = modifier,
        isErrorInitial = listViewState.isErrorInitial,
        isLoadingMore = listViewState.isLoadingMore,
        isErrorMore = listViewState.isErrorMore,
        onRetry = listViewState.triggerRefresh,
        reverseLayout = false,
    ) {
        content()

        /**
         * This is the place where we can add the items to the list
         * Example usage of detail section...
         */
//
//        items(items = items, key = { it.id }, itemContent = { itemDetail ->
//
//        })
    }

}
