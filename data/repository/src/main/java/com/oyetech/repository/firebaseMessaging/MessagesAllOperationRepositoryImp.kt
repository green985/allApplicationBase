package com.oyetech.repository.firebaseMessaging

import com.oyetech.dao.firebaseMessaging.MessagesAllDao
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-25.02.2025-
-23:31-
 **/

class MessagesAllOperationRepositoryImp(private val messagesAllDao: MessagesAllDao) :
    MessagesAllOperationRepository {

    override fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>> {
        return messagesAllDao.getMessageListFlow(conversationId)
    }

    override fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData> {
        return messagesAllDao.getMessageListWithLastMessageId(conversationId, messageId)
    }

    override fun getMessageWithId(messageId: String): FirebaseMessagingLocalData? {
        return messagesAllDao.getMessageWithId(messageId)
    }

    override fun deleteLastList(idList: List<String>): Int {
        return messagesAllDao.deleteLastList(idList)
    }

    override fun deleteAllMessages() {
        messagesAllDao.deleteAllList()
    }

    override fun getMessageListWithMessageIdList(messageIdList: List<String>): List<FirebaseMessagingLocalData> {
        return messagesAllDao.findMessageListWithMessageIdList(messageIdList)
    }

    override suspend fun insertLastList(list: List<FirebaseMessagingLocalData>) {
        messagesAllDao.insertLastList(list)
    }

    override suspend fun insertMessage(message: FirebaseMessagingLocalData) {
        messagesAllDao.insert(message)
    }
}