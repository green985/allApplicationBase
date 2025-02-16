package com.oyetech.composebase.sharedScreens.messaging

/**
Created by Erdi Özbek
-16.02.2025-
-19:00-
 **/

data class MessageDetailUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val messageText: String = "",
)

sealed class MessageDetailEvent {
    data class OnMessageTextChange(val messageText: String) : MessageDetailEvent()
    object OnMessageSend : MessageDetailEvent()
}