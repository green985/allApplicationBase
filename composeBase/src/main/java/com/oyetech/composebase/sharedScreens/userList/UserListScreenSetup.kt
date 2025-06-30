package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseGenericList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemView
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-26.02.2025-
-20:26-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName")
@Composable
fun UserListScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<UserListVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    val lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = {})


    UserListScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        navigationRoute = navigationRoute,
        listViewState = listViewState,
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName", "LongParameterList")
@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    uiState: UserListUiState,
    onEvent: (UserListEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    listViewState: GenericListState<UserListItemUiState>,
) {
    val lazyListState = rememberLazyListState()

    BaseScaffold(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            // Content can be added here if needed
            PullToRefreshBox(
                modifier = modifier,
                isRefreshing = listViewState.isRefreshing,
                onRefresh = { listViewState.triggerRefresh?.invoke() },
            ) {
                LazyColumn(
                    state = lazyListState,
                    content = {
                        items(
                            items = listViewState.items,
                            key = { it.userId },
                            itemContent = { itemDetail ->
                                UserListItemView(modifier = Modifier.clickable {
//                                navigationRoute.invoke(
//                                    QuoteAppProjectRoutes.UserProfile.withArgs(
//                                        ScreenKey.receiverUserId to itemDetail.userId,
//                                    )
//                                )
                                    navigationRoute.invoke(
                                        QuoteAppProjectRoutes.MessageDetail.withArgs(
                                            ScreenKey.receiverUserId to itemDetail.userId,
                                        )
                                    )
                                }, uiState = itemDetail)
                            })
//
//                        if (isLoadingMore) {
//                            Timber.d("LoadableLazyColumn: isLoadingMore")
//                            loadMoreLoadingContent?.invoke()
//                        }
//                        if (isErrorMore) {
//                            Timber.d("LoadableLazyColumn: isErrorMore")
//                            ErrorOnMoreContent(onRetry = onRetry)
//                        }
                    },
                )
            }
        }
    })

//
//BaseScaffold(topBarContent = {
//    RadioToolbarSetup(
//        uiState = RadioToolbarState(title = LanguageKey.connectWithPeople),
//    )
//}) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(it)
//    ) {
//
//        GenericListScreenSetup2(
//            lazyColumnState = lazyColumnState,
//            modifier = Modifier
//                .fillMaxSize(),
//            viewModel = baseListViewModel,
//            content = {
//                items(
//                    items = listViewState.items,
//                    key = { it.userId },
//                    itemContent = { itemDetail ->
//                        UserListItemView(modifier = Modifier.clickable {
////                                navigationRoute.invoke(
////                                    QuoteAppProjectRoutes.UserProfile.withArgs(
////                                        ScreenKey.receiverUserId to itemDetail.userId,
////                                    )
////                                )
//                            navigationRoute.invoke(
//                                QuoteAppProjectRoutes.MessageDetail.withArgs(
//                                    ScreenKey.receiverUserId to itemDetail.userId,
//                                )
//                            )
//                        }, uiState = itemDetail)
//                    })
//            })
//    }
//}

}