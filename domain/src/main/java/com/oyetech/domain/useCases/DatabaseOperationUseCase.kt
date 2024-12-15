package com.oyetech.domain.useCases

import com.oyetech.core.coroutineHelper.launchCustom
import com.oyetech.domain.repository.MessagesRepository
import com.oyetech.models.entity.messages.MessageConversationDataResponse
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.messages.MessagesLimitInfoResponseData
import com.oyetech.models.entity.messages.MessagesStatusOperationSocketResponse
import com.oyetech.models.entity.unreadCount.UnreadMessagesData
import com.oyetech.models.entity.user.UserOnlineOfflineStatusData
import com.oyetech.models.entity.user.UserTypingSendingOperationStatusData
import com.oyetech.models.mappers.mapToMessageDetailResponse
import com.oyetech.models.postBody.messages.DeleteConversationRequestBody
import com.oyetech.models.postBody.messages.DeleteMessageRequestBody
import com.oyetech.models.postBody.messages.MessageRequestBody
import com.oyetech.models.utils.states.MessagesState
import com.oyetech.models.utils.states.SocketUserOperation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
Created by Erdi Özbek
-20.03.2022-
-16:52-
 **/

@Suppress("TooManyFunctions")

class DatabaseOperationUseCase(private var messagesRepository: MessagesRepository) {

    // will use for message sending
    val messageSentChannel = BroadcastChannel<MessageDetailDataResponse>(Channel.BUFFERED)
    val messageDetailSendingChannel = BroadcastChannel<MessageDetailDataResponse>(Channel.BUFFERED)

    val messageDeleteSendingChannel = BroadcastChannel<DeleteMessageRequestBody>(Channel.BUFFERED)
    val conversationDeleteSendingChannel =
        BroadcastChannel<DeleteConversationRequestBody>(Channel.BUFFERED)

    val imageMessageSendingChannel = BroadcastChannel<MessageRequestBody>(Channel.BUFFERED)

    val userOnlineOfflineStatusChannel =
        BroadcastChannel<UserOnlineOfflineStatusData>(Channel.BUFFERED)
    val userTypingSendingOperationChannel =
        BroadcastChannel<UserTypingSendingOperationStatusData>(Channel.BUFFERED)

    val userMessageLimitChannel = BroadcastChannel<MessagesLimitInfoResponseData>(Channel.BUFFERED)
    val userConversationLimitChannel =
        BroadcastChannel<MessagesLimitInfoResponseData>(Channel.BUFFERED)
    val translateLimitStatusChannel =
        BroadcastChannel<MessagesLimitInfoResponseData>(Channel.BUFFERED)
    val translateLimitStatusWithMessageId = BroadcastChannel<Long>(Channel.BUFFERED)

    private val messageReceiveChannel =
        BroadcastChannel<MessageDetailDataResponse>(Channel.BUFFERED)

    fun onMessageReceiveChannelFlow(): Flow<MessageDetailDataResponse> {
        return messageReceiveChannel.openSubscription().consumeAsFlow()
    }

    fun onMessageDeleteSendingChannel(): Flow<DeleteMessageRequestBody> {
        return messageDeleteSendingChannel.openSubscription().consumeAsFlow()
    }

    fun onUserMessageLimitStatusChannel(): Flow<MessagesLimitInfoResponseData> {
        return userMessageLimitChannel.openSubscription().consumeAsFlow()
    }

    fun onUserConversationLimitStatusChannel(): Flow<MessagesLimitInfoResponseData> {
        return userConversationLimitChannel.openSubscription().consumeAsFlow()
    }

    fun onTranslateLimitStatusChannel(): Flow<MessagesLimitInfoResponseData> {
        return translateLimitStatusChannel.openSubscription().consumeAsFlow()
    }

    fun onTranslateLimitStatusChannelWithMessageId(): Flow<Long> {
        return translateLimitStatusWithMessageId.openSubscription().consumeAsFlow()
    }

    fun onUserOnlineOfflineStatusChannel(): Flow<UserOnlineOfflineStatusData> {
        return userOnlineOfflineStatusChannel.openSubscription().consumeAsFlow()
    }

    fun onConversationDeleteSendingChannel(): Flow<DeleteConversationRequestBody> {
        return conversationDeleteSendingChannel.openSubscription().consumeAsFlow()
    }

    // just use for "MessageReceived" socket channel
    suspend fun onMessageReceiveChannel(message: MessageDetailDataResponse) {
        return messageReceiveChannel.send(message)
    }

    fun onMessageSentEventFlow(): Flow<MessageDetailDataResponse> {
        return messageSentChannel.openSubscription().consumeAsFlow()
    }

    fun onMessageDetailSendFlow(): Flow<MessageDetailDataResponse> {
        return messageDetailSendingChannel.openSubscription().consumeAsFlow()
    }

    fun onUserTypingSendingOperationFlow(): Flow<UserTypingSendingOperationStatusData> {
        return userTypingSendingOperationChannel.openSubscription().consumeAsFlow()
    }

    suspend fun getMessagesOnErrorFlow(): List<MessageDetailDataResponse> {
        return messagesRepository.getMessagesOnErrorFlow()
    }

    suspend fun getMessagesNotReadFlow(
        conversationId: Long,
    ): Flow<List<MessageDetailDataResponse>> {
        return messagesRepository.getMessagesNotReadFlow(conversationId)
    }

    suspend fun onMessageReceivedTrigger(message: MessageDetailDataResponse) {
        Timber.d("onMessageReceivedTrigger = " + message.tempId)
        insertMessageReceivedToDB(message)
    }

    fun onMessageSendTrigger(message: MessageRequestBody, @MessagesState status: Int = 0) {
        GlobalScope.launchCustom {
            var transformData = message.mapToMessageDetailResponse(status)
            transformData.ownMessage = true
            insertMessageToDB(transformData)

            if (status == 0) {
                messageDetailSendingChannel.send(transformData)
            }
        }
    }

    private suspend fun insertMessageToDB(message: MessageDetailDataResponse) {
        messagesRepository.insertMessagePropertyToConversationList(message)
        messagesRepository.insertSingleMessagesToDB(message)
    }

    /**
     * This will be trigger when
     */

    suspend fun insertMessageReceivedToDB(message: MessageDetailDataResponse) {
        Timber.d("insertMessageReceivedToDB")
        messagesRepository.insertMessagePropertyToConversationList(message)
        messagesRepository.insertSingleMessagesToDB(message)
    }

    suspend fun onMessageDeliveredTrigger(eventString: MessagesStatusOperationSocketResponse?) {
        Timber.d("onMessageDeliveredTrigger")
        messagesRepository.insertMessageDeliveredToDB(eventString)
    }

    suspend fun onMessageSeenTrigger(eventString: MessagesStatusOperationSocketResponse?) {
        Timber.d("onMessageSeenTrigger")
        messagesRepository.insertMessageSeenToDB(eventString)
    }

    suspend fun onMessageSentTrigger(eventString: MessagesStatusOperationSocketResponse?) {
        Timber.d("onMessageSeenTrigger")
        messagesRepository.insertMessageSentToDB(eventString)
    }

    suspend fun onMessageReadTriggerWithList(list: List<MessageDetailDataResponse>) {
        Timber.d("onMessageReadTriggerWithList == " + list.size)
        messagesRepository.insertMessageReadListToDB(list)
    }

    suspend fun onMessageReceivedTriggerForNotification(message: MessageDetailDataResponse) {
        Timber.d("onMessageReceivedTriggerForNotification" + message)
        messagesRepository.insertMessagePropertyToConversationList(message)
        messagesRepository.insertMessageReceivedToDBWithNotification(message)
    }

    suspend fun onMessageDeletedTrigger(eventString: DeleteMessageRequestBody) {
        Timber.d("onMessageDeletedTrigger")
        messagesRepository.insertMessageDeletedToDB(eventString)
        messageDeleteSendingChannel.send(eventString)
    }

    suspend fun onMessageDeleteWithRowId(eventString: DeleteMessageRequestBody) {
        Timber.d("onMessageDeleteWithRowId")
        messagesRepository.insertMessageDeletedToDBWithRowId(eventString)
        messageDeleteSendingChannel.send(eventString)
    }

    suspend fun onMessageDeleteWithTempId(eventString: String) {
        Timber.d("onMessageDeleteWithRowId")
        var rowId = messagesRepository.getMessageRowId(eventString)
        var deleteMessageRequestBody =
            DeleteMessageRequestBody(messageIds = listOf(rowId), rowIds = listOf(rowId))
        messagesRepository.insertMessageDeletedToDBWithRowId(deleteMessageRequestBody)
        messageDeleteSendingChannel.send(deleteMessageRequestBody)
    }

    suspend fun onConversationDeletedTrigger(eventString: DeleteConversationRequestBody) {
        Timber.d("onConversationDeletedTrigger")
        messagesRepository.insertConversationDeletedToDB(eventString)
        conversationDeleteSendingChannel.send(eventString)
    }

    suspend fun onUserOnlineOfflineStatusTrigger(eventString: UserOnlineOfflineStatusData) {
        // Timber.d("onUserOnlineOfflineStatusTrigger == " + eventString.toString())
        userOnlineOfflineStatusChannel.send(eventString)
    }

    suspend fun onTypingTrigger(eventString: String) {
        Timber.d("typingg =" + eventString)
        var userId = eventString.toLong() ?: 0L
        var operationData = UserTypingSendingOperationStatusData(userId, SocketUserOperation.TYPING)
        userTypingSendingOperationChannel.send(operationData)
    }

    suspend fun onRecordingTrigger(eventString: String) {
        Timber.d("recording RECORDING_VOICE=" + eventString)
        var userId = eventString.toLong() ?: 0L
        var operationData =
            UserTypingSendingOperationStatusData(userId, SocketUserOperation.RECORDING_VOICE)
        userTypingSendingOperationChannel.send(operationData)
    }

    suspend fun onSendAudioTrigger(eventString: String) {
        Timber.d("onSendAudioTrigger =" + eventString)
        var userId = eventString.toLong() ?: 0L
        var operationData =
            UserTypingSendingOperationStatusData(userId, SocketUserOperation.SENDING_AUDIO)
        userTypingSendingOperationChannel.send(operationData)
    }

    suspend fun onSendImageTrigger(eventString: String) {
        Timber.d("recording SENDING_IMAGE =" + eventString)
        var userId = eventString.toLong() ?: 0L
        var operationData =
            UserTypingSendingOperationStatusData(userId, SocketUserOperation.SENDING_IMAGE)
        userTypingSendingOperationChannel.send(operationData)
    }

    suspend fun sendTranslateLimitTrigger(messageId: Long) {
        translateLimitStatusWithMessageId.send(messageId)
    }

    suspend fun getConversationListWithSearchQuery(searchQuery: String): Flow<List<MessageConversationDataResponse>> {
        return messagesRepository.getConversationListWithSearchQuery(searchQuery)
    }

    suspend fun getConversationListWithSearchQueryWithoutFlow(searchQuery: String): Flow<List<MessageConversationDataResponse>> {
        return flow {
            emit(
                messagesRepository.getConversationListWithSearchQueryWithoutFlow(
                    searchQuery
                )
            )
        }
    }

    suspend fun getConversationUnreadList(): Flow<List<UnreadMessagesData>> {
        return messagesRepository.getConversationUnreadList()
    }
}
