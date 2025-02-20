package com.oyetech.composebase.sharedScreens.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

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
    val conversationId: String = "",
    val participantList: List<FirebaseParticipantData> = emptyList(),
    val lastMessageId: String = "",
    val createdAt: Date? = null,
    val createdAtString: String = TimeFunctions.getDateFromLongWithHour(createdAt?.time ?: 0) ?: "",
    val participantUserIdList: List<String> = participantList.map { it.userId }.sorted(),
    val username: String = participantList.firstOrNull()?.username ?: "",
)

sealed class MessageConversationEvent {
//    data class OnMessageTextChange(val messageText: String) : MessageDetailEvent()
//    object OnMessageSend : MessageDetailEvent()
}

fun Flow<List<FirebaseMessageConversationData>>.mapToUiState(): Flow<List<MessageConversationUiState>> {
    return this.map {
        it.map { data ->
            MessageConversationUiState(
                conversationId = data.conversationId,
                participantList = data.participantList,
                lastMessageId = data.lastMessageId,
                createdAt = data.createdAt,
                participantUserIdList = data.participantList.map { it.userId }.sorted()
            )
        }
    }
}