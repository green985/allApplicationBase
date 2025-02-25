package com.oyetech.repository.firebaseMessaging

import com.oyetech.dao.firebaseMessaging.MessagesAllDao
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData

/**
Created by Erdi Özbek
-25.02.2025-
-23:31-
 **/

class MessagesAllOperationRepositoryImp(private val messagesAllDao: MessagesAllDao) :
    MessagesAllOperationRepository {

    override fun getFirstInMessage() {
        messagesAllDao.getFirstInMessage()
    }

    override fun getMessageListFlow(conversationId: String) {
        messagesAllDao.getMessageListFlow(conversationId)
    }

    override fun getMessageListWithLastMessageId(conversationId: String, messageId: String) {
        messagesAllDao.getMessageListWithLastMessageId(conversationId, messageId)
    }

    override fun getMessageWithId(messageId: String) {
        messagesAllDao.getMessageWithId(messageId)
    }

    override fun deleteLastList(idList: List<String>) {
        messagesAllDao.deleteLastList(idList)
    }

    override fun deleteAllList() {
        messagesAllDao.deleteAllList()
    }

    override fun findRadioStationWithStationIdList(messageIdList: List<String>) {
        messagesAllDao.findMessageListWithMessageIdList(messageIdList)
    }

    override fun insertLastList(list: List<FirebaseMessagingLocalData>) {
        messagesAllDao.insertLastList(list)
    }
}