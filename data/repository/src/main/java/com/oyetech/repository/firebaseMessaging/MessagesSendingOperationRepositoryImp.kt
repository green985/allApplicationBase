package com.oyetech.repository.firebaseMessaging

import com.oyetech.dao.firebaseMessaging.MessagesSendingDao
import com.oyetech.domain.repository.messaging.MessagesSendingOperationRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-20.02.2025-
-22:13-
 **/

class MessagesSendingOperationRepositoryImp(
    private val messagesSendingDao: MessagesSendingDao,
) : MessagesSendingOperationRepository {

    override suspend fun insertSendingMessage(message: FirebaseMessagingLocalData) {
        messagesSendingDao.insert(message)
    }

    override suspend fun getSendingMessageWithMessageId(messageId: String): FirebaseMessagingLocalData? {
        return messagesSendingDao.getMessageWithId(messageId)
    }

    // it will call when user successfully sent message or delete operation
    override fun deleteMessageFromLocal(vararg messageId: String) {
        messagesSendingDao.deleteLastList(messageId.toList())
    }

    override fun observeSendingMessages(): Flow<List<FirebaseMessagingLocalData>?> {
        return messagesSendingDao.getMessageListFlow()
    }

    override fun firstInMessageFlow(): Flow<FirebaseMessagingLocalData?> {
        return messagesSendingDao.getFirstInMessage()
    }


}
