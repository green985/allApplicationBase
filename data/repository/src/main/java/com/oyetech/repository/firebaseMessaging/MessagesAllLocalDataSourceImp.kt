package com.oyetech.repository.firebaseMessaging

import com.oyetech.dao.firebaseMessaging.MessagesAllDao
import com.oyetech.domain.repository.messaging.local.MessagesAllLocalDataSourceRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class MessagesAllLocalDataSourceImp(
    private val messagesAllDao: MessagesAllDao,
    private val dispatchers: AppDispatchers,
) :
    MessagesAllLocalDataSourceRepository {

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

    override fun getLastMessageWithConversationId(conversationId: String): FirebaseMessagingLocalData? {
        return messagesAllDao.getLastMessageWithConversationId(conversationId)
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

    override fun insertMessageWithGlobalScope(message: FirebaseMessagingLocalData) {
        GlobalScope.launch(dispatchers.io) {
            messagesAllDao.insert(message)
        }
    }

    override suspend fun getLastMessage(receiverId: String): FirebaseMessagingLocalData? {
        return messagesAllDao.getLastMessage(receiverId)
    }


}