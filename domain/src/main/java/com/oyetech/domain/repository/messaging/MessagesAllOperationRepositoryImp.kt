package com.oyetech.domain.repository.messaging

import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.messaging.local.MessagesAllLocalDataSourceRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus
import com.oyetech.models.firebaseModels.messagingModels.toLocalDataWithStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class MessagesAllOperationRepositoryImp(
    private val messagesAllDao: MessagesAllLocalDataSourceRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
) :
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

    override fun getMessagesFromRemoteAndInsertToLocal(
        conversationId: String,
    ): Flow<List<FirebaseMessagingLocalData>> {
        if (conversationId.isBlank()) {
            return flowOf(emptyList())
        }
        return firebaseMessagingRepository.getMessageListWithConversationId(conversationId)
            .map {
                it.map {
                    it.toLocalDataWithStatus(status = MessageStatus.SENT)
                }
            }
            .onEach {
                messagesAllDao.insertLastList(it)
            }
    }

    override fun getMessageListWithConversationIdWithMessageId(
        conversationId: String,
    ): Flow<List<FirebaseMessagingLocalData>> {
        if (conversationId.isBlank()) {
            return flowOf(emptyList())
        }
        return firebaseMessagingRepository.getMessageListWithConversationIdWithMessageId(
            conversationId
        ).map {
            it.map {
                it.toLocalDataWithStatus(status = MessageStatus.SENT)
            }
        }.onEach {
            messagesAllDao.insertLastList(it)
        }

    }

    override fun getMessageWithId(messageId: String): FirebaseMessagingLocalData? {
        return messagesAllDao.getMessageWithId(messageId)
    }

    override fun deleteLastList(idList: List<String>): Int {
        return messagesAllDao.deleteLastList(idList)
    }

    override fun deleteAllMessages() {
        messagesAllDao.deleteAllMessages()
    }

    override fun getMessageListWithMessageIdListFromLocal(messageIdList: List<String>): List<FirebaseMessagingLocalData> {
        return messagesAllDao.getMessageListWithMessageIdList(messageIdList)
    }

    override suspend fun insertLastList(list: List<FirebaseMessagingLocalData>) {
        messagesAllDao.insertLastList(list)
    }

    override suspend fun insertMessage(message: FirebaseMessagingLocalData) {
        messagesAllDao.insertMessage(message)
    }

    override fun getConversationList(): Flow<List<FirebaseMessageConversationData>> {
        return firebaseMessagingRepository.getConversationList().onEach {
            val messageIdList = it.map { it.lastMessageId }
            val messageList = getMessageListWithMessageIdListFromLocal(messageIdList)
            it.forEach { conversation ->
                conversation.lastMessage =
                    messageList.find { it.messageId == conversation.lastMessageId }
            }
            it
        }
    }

    private suspend fun getLastMessage(): FirebaseMessagingLocalData? {
        return messagesAllDao.getLastMessage("")
    }
}