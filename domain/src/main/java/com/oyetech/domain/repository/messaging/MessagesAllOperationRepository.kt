package com.oyetech.domain.repository.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

interface MessagesAllOperationRepository {
    fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>>
    fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData>

    fun getMessageWithId(messageId: String): FirebaseMessagingLocalData?
    fun getMessageListWithMessageIdList(messageIdList: List<String>): List<FirebaseMessagingLocalData>

    suspend fun insertLastList(list: List<FirebaseMessagingLocalData>)
    suspend fun insertMessage(message: FirebaseMessagingLocalData)

    fun deleteLastList(idList: List<String>): Int
    fun deleteAllMessages()
}
