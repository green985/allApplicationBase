package com.oyetech.composebase.sharedScreens.messaging.conversationList

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-17.02.2025-
-21:41-
 **/

data class MessageConversationListUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
)

sealed class MessageConversationListEvent : BaseEvent() {
    data class OnConversationClick(val conversationId: String, val userId: String) :
        MessageConversationListEvent()

    data class OnConversationClickWithPosition(val conversationPosition: Int) :
        MessageConversationListEvent()

    object Retry : MessageConversationListEvent()
    object OnConversationScreenOpen : MessageConversationListEvent()
}