package com.oyetech.composebase.sharedScreens.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Date

/**
Created by Erdi Özbek
-16.02.2025-
-19:00-
 **/

data class MessageDetailScreenUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val messageText: String = "",

    val createdAt: Date?,
    val createdAtString: String,
    val senderId: String = "",
    val receiverId: String = "",
    val conversationId: String = "",
)

data class MessageDetailUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val messageText: String = "",
    val createdAt: Long? = null,
    val createdAtString: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageId: String = "",
    val status: MessageStatus = MessageStatus.IDLE,
    val conversationId: String = "",
)

sealed class MessageDetailEvent {
    data class OnMessageTextChange(val messageText: String) : MessageDetailEvent()
    object OnMessageSend : MessageDetailEvent()
    object OnRetry : MessageDetailEvent()
    object OnRefresh : MessageDetailEvent()
}

data class MessageConversationUiState(
    val conversationId: String = "",
    val participantList: List<FirebaseParticipantData> = emptyList(),
    val lastMessageId: String = "",
    val createdAt: Date? = null,
    val createdAtString: String = TimeFunctions.getDateFromLongWithHour(createdAt?.time ?: 0) ?: "",
    val participantUserIdList: List<String> = participantList.map { it.userId }.sorted(),
    val username: String = participantList.firstOrNull()?.username ?: "",
    val usernameId: String = "",
)

sealed class MessageConversationEvent {
//    data class OnMessageTextChange(val messageText: String) : MessageDetailEvent()
//    object OnMessageSend : MessageDetailEvent()
}

fun Flow<List<FirebaseMessageConversationData>>.mapFromLocalToUiState(clientUserId: String): Flow<List<MessageConversationUiState>> {
    if (clientUserId.isEmpty()) return this.map {
        Timber.d("clientUserId is empty")
        emptyList()
    }
    return this.map {
        it.map { data ->
            MessageConversationUiState(
                conversationId = data.conversationId,
                participantList = data.participantList,
                lastMessageId = data.lastMessageId,
                createdAt = data.createdAt,
                participantUserIdList = data.participantList.map { it.userId }.sorted(),
                username = data.participantList.firstOrNull { it.userId != clientUserId }?.username
                    ?: "",
                usernameId = data.participantList.firstOrNull { it.userId != clientUserId }?.userId
                    ?: ""
            )
        }
    }
}

fun Flow<List<FirebaseMessagingResponseData>>.mapFromFirebaseToUiState(): Flow<List<MessageDetailUiState>> {
    return this.map {
        it.map { data ->
            MessageDetailUiState(
//                isLoading = data.isLoading,
//                errorText = data.errorText,
                messageText = data.messageText,
                createdAt = data.createdAt?.time,
                createdAtString = TimeFunctions.getDateFromLongWithHour(data.createdAt?.time ?: 0)
                    ?: "",
                senderId = data.senderId,
                receiverId = data.receiverId,
                messageId = data.messageId,
                status = data.status,
                conversationId = data.conversationId,
            )
        }
    }
}

fun Flow<List<FirebaseMessagingLocalData>>.mapFromLocalToUiState(): Flow<List<MessageDetailUiState>> {
    return this.map {
        it.map { data ->
            MessageDetailUiState(
//                isLoading = data.isLoading,
//                errorText = data.errorText,
                messageText = data.messageText,
                createdAt = data.createdAt,
                createdAtString = TimeFunctions.getDateFromLongWithHour(data.createdAt ?: 0)
                    ?: "",
                senderId = data.senderId,
                receiverId = data.receiverId,
                messageId = data.messageId,
                status = data.status,
                conversationId = data.conversationId,
            )
        }
    }
}
