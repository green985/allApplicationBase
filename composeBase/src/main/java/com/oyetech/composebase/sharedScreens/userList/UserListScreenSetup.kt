package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-26.02.2025-
-20:26-
 **/

@Suppress("FunctionName")
@Composable
fun UserListScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<UserListVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val complexItemViewState by vm.complexItemViewState.collectAsStateWithLifecycle()
    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            vm.handleListEvent(ListUIEvent.LoadMore)
        },
    )
    UserListScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        lazyListState = lazyListState,
        navigationRoute = navigationRoute,
        complexItemViewState = complexItemViewState,
//        onRetry = { vm.onEvent(MessageDetailEvent.OnRetry) },
//        onRefresh = { vm.onEvent(MessageDetailEvent.OnRefresh) },
    )

}

@Suppress("FunctionName")
@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    uiState: UserListUiState,
    onEvent: (UserListEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    complexItemViewState: ComplexItemListState<UserListItemUiState>,
    lazyListState: LoadableLazyColumnState,
) {
    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {
            Button(
                onClick = {
                    onEvent(UserListEvent.RegisterToUserList)
                }
            ) {
                Text("Get Users")
            }
            val items = complexItemViewState.items
            LoadableLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                isRefreshing = false,
                isLoadingInitial = complexItemViewState.isLoadingInitial,
                isErrorInitial = complexItemViewState.isErrorInitial,
//                onRetry = onRetry,
                isEmptyList = complexItemViewState.isEmptyList,
//                onRefresh = onRefresh,
                errorMessage = complexItemViewState.errorMessage,
                content = {
                    items(items = items, key = { it.userId }, itemContent = { userDetail ->
                        Column {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                text = userDetail.toString()
                            )
                        }
                    })

                },
            )
        }
    }

}