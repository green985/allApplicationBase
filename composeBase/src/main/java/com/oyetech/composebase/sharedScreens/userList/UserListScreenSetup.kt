package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.clickable
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
import com.oyetech.composebase.base.baseGenericList.GenericListScreenSetup
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
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
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()


    UserListScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        listViewState = listViewState,
        navigationRoute = navigationRoute,
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
            GenericListScreenSetup(
                modifier = Modifier.fillMaxSize(),
                listViewState = listViewState,
                {
                    items(
                        items = listViewState.items,
                        key = { it.userId },
                        itemContent = { itemDetail ->
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        navigationRoute.invoke(
                                            QuoteAppProjectRoutes.MessageDetail.withArgs(
                                                ScreenKey.receiverUserId to itemDetail.userId,
                                            )
                                        )
                                    }
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                text = itemDetail.toString()
                            )
                        })
                },
                true
            )
        }
    }

}