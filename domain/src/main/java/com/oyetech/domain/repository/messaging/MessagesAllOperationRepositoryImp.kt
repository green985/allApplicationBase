package com.oyetech.domain.repository.messaging

import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus
import com.oyetech.models.firebaseModels.messagingModels.toLocalDataWithStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class MessagesAllOperationRepositoryImp(
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
) : MessagesAllOperationRepository {

    override var currentConversationId: MutableStateFlow<String> = MutableStateFlow("")
    override var currentUsername: String = ""

    override fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>> {
        return flowOf(emptyList())
    }

    override fun getMessageListWithReceiverId(receiverId: String): Flow<List<FirebaseMessagingLocalData>> {
        return flowOf(emptyList())
    }

    override fun insertMessageWithGlobalScope(message: FirebaseMessagingLocalData) {
        // no-op: local storage removed
    }

    override fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData> {
        return emptyList()
    }

    override fun getMessagesFromRemoteAndInsertToLocal(
        conversationId: String,
    ): Flow<List<FirebaseMessagingLocalData>> {
        if (conversationId.isBlank()) {
            return flowOf(emptyList())
        }
        return firebaseMessagingRepository.getMessageListWithConversationId(conversationId)
            .map { it.map { msg -> msg.toLocalDataWithStatus(status = MessageStatus.SENT) } }
    }

    override fun getMessageListWithConversationIdWithMessageId(
        conversationId: String,
    ): Flow<List<FirebaseMessagingLocalData>> {
        if (conversationId.isBlank()) {
            return flowOf(emptyList())
        }
        return firebaseMessagingRepository.getMessageListWithConversationIdWithMessageId(
            conversationId
        ).map { it.map { msg -> msg.toLocalDataWithStatus(status = MessageStatus.SENT) } }
    }

    override fun getMessageWithId(messageId: String): FirebaseMessagingLocalData? {
        return null
    }

    override fun deleteLastList(idList: List<String>): Int {
        return 0
    }

    override fun deleteAllMessages() {
        // no-op: local storage removed
    }

    override suspend fun getMessageListWithMessageIdListFromLocal(messageIdList: List<String>): List<FirebaseMessagingLocalData> {
        return emptyList()
    }

    override suspend fun insertLastList(list: List<FirebaseMessagingLocalData>) {
        // no-op: local storage removed
    }

    override suspend fun insertMessage(message: FirebaseMessagingLocalData) {
        // no-op: local storage removed
    }

    override fun getConversationList(): Flow<List<FirebaseMessageConversationData>> {
        return firebaseMessagingRepository.getConversationList()
    }

    override fun getConversationListUpdated(): Flow<List<FirebaseMessageConversationData>> {
        return firebaseMessagingRepository.getConversationListUpdated()
    }
}
