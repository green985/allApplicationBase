package com.oyetech.domain.repository.messaging.local

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

interface MessagesAllLocalDataSourceRepository {
    fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>>
    fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData>

    fun getMessageWithId(messageId: String): FirebaseMessagingLocalData?
    fun deleteLastList(idList: List<String>): Int
    fun deleteAllMessages()
    fun getMessageListWithMessageIdList(messageIdList: List<String>): List<FirebaseMessagingLocalData>

    suspend fun insertLastList(list: List<FirebaseMessagingLocalData>)

    suspend fun insertMessage(message: FirebaseMessagingLocalData)
    fun insertMessageWithGlobalScope(message: FirebaseMessagingLocalData)
    suspend fun getLastMessage(receiverId: String): FirebaseMessagingLocalData?
    fun getLastMessageWithConversationId(conversationId: String): FirebaseMessagingLocalData?
}