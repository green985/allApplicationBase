package com.oyetech.domain.repository.firebase.realtime

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseRealtimeHelperRepository {
    val realtimeMessageSendOperationResultState: MutableStateFlow<FirebaseMessagingResponseData?>
    fun sendMessageWithRealtime(messageBody: FirebaseMessagingResponseData)
    suspend fun observeUserMessagesRealtimeOperations(): Flow<Unit>
}
