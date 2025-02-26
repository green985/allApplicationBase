package com.oyetech.composebase.sharedScreens.userList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
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

    UserListScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) }
    )

}

@Suppress("FunctionName")
@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    uiState: UserListUiState,
    onEvent: (UserListEvent) -> (Unit),
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
        }
    }

}