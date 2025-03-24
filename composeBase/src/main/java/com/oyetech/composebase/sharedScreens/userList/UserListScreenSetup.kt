package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListScreenSetup2
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseGenericList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemView
import com.oyetech.languageModule.keyset.LanguageKey
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
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    val lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = {})


    UserListScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        navigationRoute = navigationRoute,
        lazyColumnState = lazyColumnState,
        listViewState = listViewState,
        baseListViewModel = vm,
    )

}

@Suppress("FunctionName")
@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    uiState: UserListUiState,
    onEvent: (UserListEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    listViewState: GenericListState<UserListItemUiState>,
    baseListViewModel: BaseListViewModel<UserListItemUiState>,
    lazyColumnState: LoadableLazyColumnState,
) {
    BaseScaffold(topBarContent = {
        RadioToolbarSetup(
            uiState = RadioToolbarState(title = LanguageKey.connectWithPeople),
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

//            Button(
//                onClick = {
//                    onEvent(UserListEvent.RemoveUserFromList)
//                }
//            ) {
//                Text("Remove from list")
//            }

            GenericListScreenSetup2(
                lazyColumnState = lazyColumnState,
                modifier = Modifier
                    .fillMaxSize(),
                viewModel = baseListViewModel,
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
                })
        }
    }

}