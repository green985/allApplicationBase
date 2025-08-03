package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemView
import com.oyetech.languageModule.keyset.LanguageKey
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


    BaseScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = LanguageKey.userFeedListTitle,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // Content can be added here if needed
                    PullToRefreshBox(
                        modifier = modifier,
                        isRefreshing = listViewState.isRefreshing,
                        onRefresh = {
                            onEvent.invoke(UserListEvent.RefreshUserList)
                        },
                    ) {
                        LazyColumn(
                            state = lazyListState,
                            content = {
                                itemsIndexed(
                                    items = listViewState.items,
                                    itemContent = { index, itemDetail ->
                                        UserListItemView(modifier = Modifier.clickable {
                                            onEvent.invoke(UserListEvent.OnUserClick(index))
                                        }, uiState = itemDetail)
                                    })
                            },
                        )



                        if (listViewState.isLoadingInitial) {
                            LoadingScreenFullSize()
                        }

                        if (listViewState.isErrorInitial) {

                            ErrorScreenFullSize(
                                errorMessage = listViewState.errorMessage,
                                withoutAlpha = true
                            )
                        }
                        if (listViewState.isEmptyList) {
                            ErrorScreenFullSize(
                                errorMessage = LanguageKey.conversationNotFound,
                                withoutAlpha = true
                            )
                        }
                    }
                }
            }
        })

}