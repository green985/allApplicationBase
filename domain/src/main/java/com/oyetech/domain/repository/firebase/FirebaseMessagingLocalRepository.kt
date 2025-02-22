package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

interface FirebaseMessagingLocalRepository {

    suspend fun insertMessage(message: FirebaseMessagingLocalData)
    suspend fun getMessage(messageId: String): FirebaseMessagingLocalData?
    fun deleteMessageFromLocal(vararg messageId: String)

    fun observeSendingMessages(): Flow<List<FirebaseMessagingLocalData>?>
    fun firstInMessageFlow(): Flow<FirebaseMessagingLocalData?>
}
