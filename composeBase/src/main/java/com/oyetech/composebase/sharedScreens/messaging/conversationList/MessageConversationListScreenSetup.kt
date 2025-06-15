package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationClick
import com.oyetech.composebase.sharedScreens.messaging.views.MessageConversationItemView
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

    val listViewState by vm.listViewState.collectAsStateWithLifecycle()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            MessageConversationListScreen(
                uiState = uiState,
                contentPadding = innerPadding,
                onEvent = { vm.onEvent(it) },
                listViewState = listViewState,
                navigationRoute = navigationRoute,
            )
        })

}

@Suppress("FunctionName")
@Composable
fun MessageConversationListScreen(
    contentPadding: PaddingValues,
    uiState: MessageConversationListUiState,
    onEvent: (MessageConversationListEvent) -> (Unit),
    listViewState: GenericListState<MessageConversationUiState>,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = Modifier.padding(contentPadding)) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.padding(4.dp),
                state = lazyListState,
                reverseLayout = true,
                content = {
                    items(
                        items = listViewState.items,
                        key = { it.conversationId },
                        itemContent = { itemDetail ->
                            MessageConversationItemView(
                                modifier = Modifier.clickable {
                                    onEvent(OnConversationClick(itemDetail.conversationId))
                                    navigationRoute.invoke(
                                        QuoteAppProjectRoutes.MessageDetail.withArgs(
                                            ScreenKey.conversationId to itemDetail.conversationId,
                                            ScreenKey.receiverUserId to itemDetail.userId,
                                        )
                                    )
                                },
                                uiState = itemDetail,
                            )
                        })
                },
            )
        }

    }
}