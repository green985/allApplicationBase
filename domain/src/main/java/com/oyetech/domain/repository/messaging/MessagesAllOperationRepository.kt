package com.oyetech.domain.repository.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

interface MessagesAllOperationRepository {
    fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>>

    //    fun getMessageListWithUserIdFlow(userId:String): Flow<List<FirebaseMessagingLocalData>>
    fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData>

    fun getMessageWithId(messageId: String): FirebaseMessagingLocalData?
    fun getMessageListWithMessageIdListFromLocal(messageIdList: List<String>): List<FirebaseMessagingLocalData>

    suspend fun insertLastList(list: List<FirebaseMessagingLocalData>)
    suspend fun insertMessage(message: FirebaseMessagingLocalData)

    fun deleteLastList(idList: List<String>): Int
    fun deleteAllMessages()
    fun getConversationList(): Flow<List<FirebaseMessageConversationData>>
    fun getMessagesFromRemoteAndInsertToLocal(conversationId: String): Flow<List<FirebaseMessagingLocalData>>
}
