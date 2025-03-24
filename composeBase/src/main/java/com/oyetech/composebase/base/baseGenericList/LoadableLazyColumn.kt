package com.oyetech.composebase.base.baseGenericList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Vertical
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Horizontal
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.PagingMoreLoading
import timber.log.Timber

@Suppress("LongParameterList", "CyclomaticComplexMethod", "FunctionNaming", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadableLazyColumn(
    modifier: Modifier = Modifier,
    lazyColumnState: LoadableLazyColumnState,
    isRefreshing: Boolean,
    isLoadingInitial: Boolean,
    isRefreshEnable: Boolean = false,
    isErrorInitial: Boolean = false,
    isLoadingMore: Boolean = false,
    isErrorMore: Boolean = false,
    skipInitialLoading: Boolean = false,
    isEmptyList: Boolean = false,
    errorMessage: String = "",
    onRetry: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    loadMoreLoadingContent: @Composable() (() -> Unit)? = { PagingMoreLoading() },
    onItemVisible: (Int) -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    val lazyListState = lazyColumnState.lazyListState
    val listLayoutInfo by remember { derivedStateOf { lazyListState.layoutInfo } }


    Column() {
        if (!isRefreshEnable) {
            Column(modifier = modifier) {
                initLazyColumn(
                    contentPadding,
                    lazyColumnState,
                    reverseLayout,
                    verticalArrangement,
                    horizontalAlignment,
                    flingBehavior,
                    userScrollEnabled,
                    content,
                    isLoadingInitial,
                    isErrorMore,
                    isErrorInitial,
                    loadMoreLoadingContent,
                    isLoadingMore,
                    onRetry
                )
            }
        } else {
            PullToRefreshBox(
                modifier = modifier,
                isRefreshing = isRefreshing,
                onRefresh = { onRefresh?.let { it() } },
            ) {
                initLazyColumn(
                    contentPadding,
                    lazyColumnState,
                    reverseLayout,
                    verticalArrangement,
                    horizontalAlignment,
                    flingBehavior,
                    userScrollEnabled,
                    content,
                    isLoadingInitial,
                    isErrorMore,
                    isErrorInitial,
                    loadMoreLoadingContent,
                    isLoadingMore,
                    onRetry
                )
            }
        }


        if (isLoadingInitial) {
            if (!skipInitialLoading) {
                LoadingScreenFullSize(modifier)
            }
        } else {
            RadioErrorListView(
                errorMessage = errorMessage,
                isErrorInitial = isErrorInitial,
                isEmptyList = isEmptyList,
                onRetry = onRetry
            )
        }

    }
    var lastTimeIsScrollInProgress by remember {
        mutableStateOf(lazyListState.isScrollInProgress)
    }
    var lastTimeLastVisibleIndex by remember {
        mutableStateOf(listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
    }
    val currentIsScrollInProgress = lazyListState.isScrollInProgress
    val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    if (!currentIsScrollInProgress && lastTimeIsScrollInProgress) {
        if (currentLastVisibleIndex != lastTimeLastVisibleIndex) {
            val isScrollDown = currentLastVisibleIndex > lastTimeLastVisibleIndex
            val remainCount = listLayoutInfo.totalItemsCount - currentLastVisibleIndex - 1
            if (isScrollDown && remainCount <= lazyColumnState.loadMoreState.loadMoreRemainCountThreshold) {
                LaunchedEffect(Unit) {
                    Timber.d(" LoadableLazyColumn: onLoadMore")
                    lazyColumnState.loadMoreState.onLoadMore()
                }
            }
        }
        lastTimeLastVisibleIndex = currentLastVisibleIndex
    }
    lastTimeIsScrollInProgress = currentIsScrollInProgress

    LaunchedEffect(currentLastVisibleIndex) {
        onItemVisible(currentLastVisibleIndex)
    }
}

@Composable
private fun initLazyColumn(
    contentPadding: PaddingValues,
    state: LoadableLazyColumnState,
    reverseLayout: Boolean,
    verticalArrangement: Vertical,
    horizontalAlignment: Horizontal,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    content: LazyListScope.() -> Unit,
    isLoadingInitial: Boolean,
    isErrorMore: Boolean,
    isErrorInitial: Boolean,
    loadMoreLoadingContent: @Composable() (() -> Unit)?,
    isLoadingMore: Boolean,
    onRetry: (() -> Unit)?,
) {
    LazyColumn(
        contentPadding = contentPadding,
        state = state.lazyListState,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        content = {
            content()


            item {
                // todo will be contunie
                if (!isLoadingInitial && !isErrorMore && !isErrorInitial) {
                    loadMoreLoadingContent?.invoke()
                }

                if (isLoadingMore) {
                    Timber.d("LoadableLazyColumn: isLoadingMore")
                    loadMoreLoadingContent?.invoke()
                }
                if (isErrorMore) {
                    Timber.d("LoadableLazyColumn: isErrorMore")
                    ErrorOnMoreContent(onRetry = onRetry)
                }
            }
        },
    )
}

@Composable
fun ErrorOnMoreContent(errorMessage: String = "Loading Error", onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(color = MaterialTheme.colorScheme.error)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.displaySmall,
            text = errorMessage,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = { onRetry?.invoke() }) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun rememberLoadableLazyColumnState(
    onLoadMore: () -> Unit,
    refreshThreshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    loadMoreRemainCountThreshold: Int = 10,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
): LoadableLazyColumnState {
    val pullRefreshState = rememberPullToRefreshState()

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
    )

    val loadMoreState = rememberLoadMoreState(loadMoreRemainCountThreshold, onLoadMore)

    return remember {
        LoadableLazyColumnState(
            lazyListState = lazyListState,
            pullRefreshState = pullRefreshState,
            loadMoreState = loadMoreState,
        )
    }
}

@Composable
fun rememberLoadMoreState(
    loadMoreRemainCountThreshold: Int,
    onLoadMore: () -> Unit,
): LoadMoreState {
    return remember {
        LoadMoreState(loadMoreRemainCountThreshold, onLoadMore)
    }
}

@Composable
fun ListCustomLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                enabled = false,
                onClick = {}), // Kullanıcı aksiyonlarını bloklamak için
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun RadioErrorListView(
    errorMessage: String = "",
    isErrorInitial: Boolean = false,
    isEmptyList: Boolean = false,
    onRetry: (() -> Unit)? = null,
) {
    if (isErrorInitial || isEmptyList) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (isErrorInitial) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(onClick = { onRetry?.invoke() }) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                if (isEmptyList) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            style = MaterialTheme.typography.displaySmall,
                            text = stringResource(R.string.searchpreference_no_results),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

        }
    }
}