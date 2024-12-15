package com.oyetech.domain.useCases

import com.oyetech.domain.repository.MessagesRepository
import com.oyetech.models.entity.messages.MessageConversationDataResponse
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.messages.MessagesConversationStatusData
import com.oyetech.models.entity.unreadCount.UnreadMessagesData
import com.oyetech.models.postBody.messages.DeleteConversationRequestBody
import com.oyetech.models.postBody.messages.DeleteMessageRequestBody
import com.oyetech.models.postBody.messages.MessagesBeforeAfterRequestBody
import com.oyetech.models.postBody.messages.MessagesConversationRequestBody
import com.oyetech.models.postBody.messages.ReportConversationRequestBody
import com.oyetech.models.postBody.messages.ReportMessagesRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
Created by Erdi Özbek
-16.03.2022-
-15:56-
 **/

class MessagesOperationUseCase(private var repository: MessagesRepository) {

    suspend fun getConversations(
        params: MessagesConversationRequestBody,
    ): Flow<List<MessageConversationDataResponse>> {
        return repository.getMessageConversations(params)
    }

    suspend fun getConversationsInStart(): Flow<List<MessageConversationDataResponse>> {
        var messageListRequestBody = MessagesConversationRequestBody(page = 1)
        return repository.getMessageConversations(messageListRequestBody)
    }

    suspend fun getMessageAfter(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>> {
        return repository.getMessageAfter(params)
    }

    suspend fun getMessageBefore(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>> {
        return repository.getMessageBefore(params)
    }

    suspend fun getMessageBeforeWithDaoFlow(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>> {
        return repository.getMessageBeforeDaoFlow(params)
    }

    suspend fun getLastNewMessageFlow(): Flow<MessageDetailDataResponse> {
        return repository.getLastNewMessageFlow()
    }

    suspend fun getReceivedMessageFromConversation(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<MessageDetailDataResponse?> {
        return repository.getReceivedMessageFromConversation(params)
    }

    suspend fun getMessageConversationFlow(): Flow<List<MessageConversationDataResponse>> {
        return repository.getMessageConversationsFlow()
    }

    suspend fun getConversationIdWithUserId(toUserId: Long): MessageConversationDataResponse? {
        return repository.getConversationIdWithUserId(toUserId)
    }

    suspend fun getConversationIdWithConversationId(conversationId: Long): MessageConversationDataResponse? {
        return repository.getConversationIdWithConversationId(conversationId)
    }

    suspend fun clearConversationUnreadCount(conversationId: Long) {
        return repository.clearConversationUnreadCount(conversationId)
    }

    suspend fun insertSingleMessage(message: MessageDetailDataResponse) {
        return repository.insertSingleMessagesToDB(message)
    }

    suspend fun getUnreadCountDataList(): Flow<List<UnreadMessagesData>> {
        return repository.getUnreadCountDataList()
    }

    suspend fun getMessageStatusWithMessageIdList(list: List<MessageConversationDataResponse>): List<MessagesConversationStatusData> {
        var conversationList = arrayListOf<Long>()

        list.forEach {
            conversationList.add(it.maxMessageId)
        }

        return repository.getMessageStatusWithMessageIdList(conversationList)
    }

    suspend fun getConversationUnreadList(): Flow<List<UnreadMessagesData>> {
        return repository.getConversationUnreadList()
    }

    suspend fun deleteMessages(params: DeleteMessageRequestBody): Flow<Boolean> {
        return repository.deleteMessages(params = params)
    }

    suspend fun getDeletedMessages(): Flow<DeleteMessageRequestBody> {
        return repository.getDeletedMessages()
    }

    suspend fun getDeletedConversation(): Flow<DeleteConversationRequestBody> {
        return repository.getDeletedConversation()
    }

    suspend fun deleteConversation(params: DeleteConversationRequestBody): Flow<Boolean> {
        return repository.deleteConversation(params = params)
    }

    suspend fun reportMessage(params: ReportMessagesRequestBody): Flow<Boolean> {
        return repository.reportMessages(params)
    }

    suspend fun reportConversation(params: ReportConversationRequestBody): Flow<Boolean> {
        return repository.reportConversation(params)
    }

    fun getTotalReceivedMessageCount(): Flow<Int> {
        var flow = flow<Int> {
            emit(repository.getReceivedMessageCount())
        }
        return flow
    }

    suspend fun isMessagingLimitExceeded(): Flow<Boolean> {
        return repository.isMessagingLimitExceeded()
    }

    suspend fun isConversationLimitExceeded(): Flow<Boolean> {
        return repository.isConversationLimitExceeded()
    }
}
