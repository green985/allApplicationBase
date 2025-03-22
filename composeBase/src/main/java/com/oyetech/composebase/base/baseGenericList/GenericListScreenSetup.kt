package com.oyetech.composebase.base.baseGenericList

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun <T> GenericListScreenSetup2(
    modifier: Modifier = Modifier,
    viewModel: BaseListViewModel<T>,
    content: LazyListScope.() -> Unit,
    reverseLayout: Boolean = false,
    lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = { viewModel.loadMore() }),
) {
    val listViewState by viewModel.listViewState.collectAsStateWithLifecycle()

    LoadableLazyColumn(
        lazyColumnState = lazyColumnState,
        isRefreshing = listViewState.isRefreshing,
        isRefreshEnable = listViewState.isRefreshEnable,
        isLoadingInitial = listViewState.isLoadingInitial,
        isEmptyList = listViewState.items.isEmpty(),
        errorMessage = listViewState.errorMessage,
        onRefresh = { viewModel.refreshList() },
        modifier = modifier,
        isErrorInitial = listViewState.isErrorInitial,
        isLoadingMore = listViewState.isLoadingMore,
        isErrorMore = listViewState.isErrorMore,
        onRetry = { viewModel.retry() },
        reverseLayout = reverseLayout,
    ) {
        content()
    }
}

@Composable
fun <T> GenericListScreenSetup(
    modifier: Modifier = Modifier,
    listViewState: GenericListState<T>,
    content: LazyListScope.() -> Unit,
    reverseLayout: Boolean = false,
    lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() }),
) {
    LoadableLazyColumn(
        lazyColumnState = lazyColumnState,
        isRefreshing = listViewState.isRefreshing,
        isLoadingInitial = listViewState.isLoadingInitial,
        isEmptyList = listViewState.items.isEmpty(),
        isRefreshEnable = listViewState.isRefreshEnable,
        errorMessage = listViewState.errorMessage,
        onRefresh = { listViewState.triggerRefresh?.invoke() },
        modifier = modifier,
        isErrorInitial = listViewState.isErrorInitial,
        isLoadingMore = listViewState.isLoadingMore,
        isErrorMore = listViewState.isErrorMore,
        onRetry = listViewState.triggerRefresh,
        reverseLayout = reverseLayout,
    ) {
        content()

        /**
         * This is the place where we can add the items to the list
         * Example usage of detail section...
         */

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


