package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
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

    val lazyPagingItems = vm.conversationPaging.collectAsLazyPagingItems()
    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            MessageConversationListScreen(
                uiState = uiState,
                onEvent = { vm.onEvent(it) },
                lazyPagingItems = lazyPagingItems,
                navigationRoute = navigationRoute,
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
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {

    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { it.conversationId },
                onBindItem = { conversation ->
                    MessageConversationItemView(
                        modifier = Modifier.clickable {
                            onEvent(OnConversationClick(conversation.conversationId))
                            navigationRoute.invoke(
                                QuoteAppProjectRoutes.MessageDetail.withArgs(
                                    ScreenKey.conversationId to conversation.conversationId,
                                    ScreenKey.receiverUserId to conversation.usernameId,
                                )
                            )
                        },
                        uiState = conversation,
                    )
                },
            )
        }
    }

}