package com.oyetech.domain.repository

import com.oyetech.models.entity.messages.MessageConversationDataResponse
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.messages.MessagesConversationStatusData
import com.oyetech.models.entity.messages.MessagesStatusOperationSocketResponse
import com.oyetech.models.entity.unreadCount.UnreadMessagesData
import com.oyetech.models.postBody.messages.DeleteConversationRequestBody
import com.oyetech.models.postBody.messages.DeleteMessageRequestBody
import com.oyetech.models.postBody.messages.MessagesBeforeAfterRequestBody
import com.oyetech.models.postBody.messages.MessagesConversationRequestBody
import com.oyetech.models.postBody.messages.ReportConversationRequestBody
import com.oyetech.models.postBody.messages.ReportMessagesRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-16.03.2022-
-15:52-
 **/
@Suppress("TooManyFunctions")
interface MessagesRepository {

    suspend fun getMessageConversations(
        params: MessagesConversationRequestBody,
    ): Flow<List<MessageConversationDataResponse>>

    suspend fun getMessageConversationsFlow(): Flow<List<MessageConversationDataResponse>>

    suspend fun getMessageBefore(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>>

    suspend fun getMessageAfter(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>>

    suspend fun getMessageBeforeDaoFlow(
        params: MessagesBeforeAfterRequestBody,
    ): Flow<List<MessageDetailDataResponse>>

    suspend fun insertSingleMessagesToDB(message: MessageDetailDataResponse)

    suspend fun insertMessageSendToDB(message: MessageDetailDataResponse)
    suspend fun insertMessageReceivedToDBWithNotification(message: MessageDetailDataResponse)
    suspend fun insertMessageDeliveredToDB(eventBody: MessagesStatusOperationSocketResponse?)
    suspend fun insertMessageSeenToDB(eventBody: MessagesStatusOperationSocketResponse?)
    suspend fun insertMessageSentToDB(eventBody: MessagesStatusOperationSocketResponse?)
    suspend fun insertMessageReadToDB(messageReadRequestBody: MessagesStatusOperationSocketResponse)
    suspend fun insertMessageReadListToDB(list: List<MessageDetailDataResponse>)

    suspend fun insertMessageDeletedToDB(eventBody: DeleteMessageRequestBody?)
    suspend fun insertConversationDeletedToDB(eventBody: DeleteConversationRequestBody)

    fun insertMessagePropertyToConversationList(message: MessageDetailDataResponse)
    fun getConversationIdWithUserId(toUserId: Long): MessageConversationDataResponse?

    suspend fun getMessagesOnErrorFlow(): List<MessageDetailDataResponse>
    suspend fun getMessagesNotReadFlow(conversationId: Long): Flow<List<MessageDetailDataResponse>>
    suspend fun getUnreadCountDataList(): Flow<List<UnreadMessagesData>>

    suspend fun deleteMessages(params: DeleteMessageRequestBody): Flow<Boolean>
    suspend fun deleteConversation(params: DeleteConversationRequestBody): Flow<Boolean>
    suspend fun getDeletedMessages(): Flow<DeleteMessageRequestBody>
    suspend fun getDeletedConversation(): Flow<DeleteConversationRequestBody>

    suspend fun reportConversation(params: ReportConversationRequestBody): Flow<Boolean>
    suspend fun reportMessages(params: ReportMessagesRequestBody): Flow<Boolean>
    suspend fun insertMessageDeletedToDBWithRowId(eventBody: DeleteMessageRequestBody?)
    suspend fun getConversationListWithSearchQuery(searchQuery: String): Flow<List<MessageConversationDataResponse>>
    suspend fun getConversationListWithSearchQueryWithoutFlow(searchQuery: String): List<MessageConversationDataResponse>
    fun clearConversationUnreadCount(conversationId: Long)
    suspend fun getConversationUnreadList(): Flow<List<UnreadMessagesData>>

    fun getConversationIdWithConversationId(conversationId: Long): MessageConversationDataResponse?
    suspend fun getReceivedMessageFromConversation(params: MessagesBeforeAfterRequestBody): Flow<MessageDetailDataResponse>
    fun getReceivedMessageCount(): Int

    suspend fun isMessagingLimitExceeded(): Flow<Boolean>
    suspend fun isConversationLimitExceeded(): Flow<Boolean>
    fun getMessageRowId(tempString: String): Long
    suspend fun getMessageStatusWithMessageIdList(list: List<Long>): List<MessagesConversationStatusData>
    suspend fun getLastNewMessageFlow(): Flow<MessageDetailDataResponse>
}
