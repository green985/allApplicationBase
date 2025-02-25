package com.oyetech.domain.repository.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

interface MessagesSendingOperationRepository {

    suspend fun insertSendingMessage(message: FirebaseMessagingLocalData)
    suspend fun getSendingMessageWithMessageId(messageId: String): FirebaseMessagingLocalData?
    fun deleteMessageFromLocal(vararg messageId: String)

    fun observeSendingMessages(): Flow<List<FirebaseMessagingLocalData>?>
    fun firstInMessageFlow(): Flow<FirebaseMessagingLocalData?>
}
