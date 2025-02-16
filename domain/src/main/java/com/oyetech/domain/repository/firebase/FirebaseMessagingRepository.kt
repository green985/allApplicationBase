package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseMessagingRepository {
    fun idlee()
    suspend fun getConversationDetailOrCreate(receiverUserId: String)
    val conversationStateFlow: MutableStateFlow<FirebaseMessageConversationData?>
}
