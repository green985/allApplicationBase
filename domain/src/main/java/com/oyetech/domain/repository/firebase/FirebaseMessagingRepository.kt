package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseMessagingRepository {
    fun idlee()
    suspend fun getConversationDetailOrCreateFlow(receiverUserId: String): Flow<FirebaseMessageConversationData>
    suspend fun getConversationList(): Flow<List<FirebaseMessageConversationData>>

    val userMessageConversationList: MutableStateFlow<List<FirebaseMessageConversationData>?>
    suspend fun sendMessage(
        messageText: String,
        conversationId: String,
        receiverUserId: String,
    ): Flow<FirebaseMessagingResponseData>

    fun initLocalMessageSendOperation(scope: CoroutineScope)

    // this function will return all message information with flow
    // realtime, so we can observe the message list and update the ui
    // when new message comes
    fun getMessageListWithConversationId(conversationId: String): Flow<List<FirebaseMessagingResponseData>>

    // will be used for load more messages
//    fun getMessagesLoadMoreWithMessageId(
//        conversationId: String,
//        messageId: String,
//    ): Flow<List<FirebaseMessagingResponseData>>
}
