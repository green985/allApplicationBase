package com.oyetech.composebase.sharedScreens.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

data class MessageConversationUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
)

sealed class MessageConversationEvent {
//    data class OnMessageTextChange(val messageText: String) : MessageDetailEvent()
//    object OnMessageSend : MessageDetailEvent()
}

fun Flow<List<FirebaseMessageConversationData>>.mapToUiState(): Flow<List<MessageConversationUiState>> {
    return this.map {
        it.map {
            MessageConversationUiState(
            )
        }
    }
}