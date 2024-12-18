package com.oyetech.composebase.base.baseList

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalLoadingView
import timber.log.Timber

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadableLazyColumn(
    modifier: Modifier = Modifier,
    state: LoadableLazyColumnState,
    isRefreshing: Boolean,
    isLoadingInitial: Boolean,
    isLoadingMore: Boolean = false,
    isErrorInitial: Boolean = false,
    isEmptyList: Boolean = false,
    errorMessage: String = "",
    onRetry: () -> Unit = { },
    onRefresh: () -> Unit = { },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    loadMoreLoadingContent: @Composable() (() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    val lazyListState = state.lazyListState
    val listLayoutInfo by remember { derivedStateOf { lazyListState.layoutInfo } }


    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
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
                    if (!isLoadingInitial) {
                        loadMoreLoadingContent?.invoke()
                    }

                    if (isLoadingMore) {
                        loadMoreLoadingContent?.invoke()
                    }
                }
            },
        )

        if (isLoadingInitial) {
            GlobalLoadingView()
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
            if (isScrollDown && remainCount <= state.loadMoreState.loadMoreRemainCountThreshold) {
                LaunchedEffect(Unit) {
                    Timber.d(" LoadableLazyColumn: onLoadMore")
                    state.loadMoreState.onLoadMore()
                }
            }
        }
        lastTimeLastVisibleIndex = currentLastVisibleIndex
    }
    lastTimeIsScrollInProgress = currentIsScrollInProgress
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun rememberLoadableLazyColumnState(
    onLoadMore: () -> Unit,
    refreshThreshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    loadMoreRemainCountThreshold: Int = 5,
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
fun RadioLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
    onRetry: () -> Unit = {},
) {
    if (isErrorInitial || isEmptyList) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (isEmptyList) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        text = stringResource(R.string.searchpreference_no_results),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if (isErrorInitial) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(onClick = onRetry) {
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
        }
    }
}