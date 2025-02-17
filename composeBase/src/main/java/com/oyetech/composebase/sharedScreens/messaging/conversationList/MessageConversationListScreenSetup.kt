package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-17.02.2025-
-21:40-
 **/

@Suppress("FunctionName")
@Composable
fun MessageConversationListScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<MessageConversationListVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val lazyPagingItems = vm.conversationPaging.collectAsLazyPagingItems()
    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            MessageConversationListScreen(
                uiState = uiState,
                onEvent = { vm.onEvent(it) },
                lazyPagingItems = lazyPagingItems
            )
        }
    }

}

@Suppress("FunctionName")
@Composable
fun MessageConversationListScreen(
    modifier: Modifier = Modifier,
    uiState: MessageConversationListUiState,
    onEvent: (MessageConversationListEvent) -> (Unit),
    lazyPagingItems: LazyPagingItems<MessageConversationUiState>,
) {

    // todo itemKey eklenecek
    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                onBindItem = { conversation ->
                    Text(text = conversation.toString())
                },
            )
        }
    }

}