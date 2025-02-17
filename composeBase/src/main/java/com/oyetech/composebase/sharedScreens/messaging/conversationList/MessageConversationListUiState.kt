package com.oyetech.composebase.sharedScreens.messaging.conversationList

/**
Created by Erdi Özbek
-17.02.2025-
-21:41-
 **/

data class MessageConversationListUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
)

sealed class MessageConversationListEvent {
    data class Idle(val data: Int) : MessageConversationListEvent()
    object Idlee : MessageConversationListEvent()
}