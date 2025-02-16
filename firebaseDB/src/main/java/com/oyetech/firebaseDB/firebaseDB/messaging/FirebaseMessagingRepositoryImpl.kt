package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-17:51-
 **/

class FirebaseMessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) : FirebaseMessagingRepository {

    override val conversationStateFlow = MutableStateFlow<FirebaseMessageConversationData?>(null)

    override fun idlee() {

    }

    override suspend fun getConversationDetailOrCreate(receiverUserId: String) {
//            val userId = userRepository.getUserId()
        val userId = "denemeFirstUser"


        require(userId.isNotBlank()) { "User not logged in" }
        require(userId != receiverUserId) { "Cannot create conversation with self" }

        val conversationRef = firestore.collection(FirebaseDatabaseKeys.conversations)

        val userLists = listOf(userId, receiverUserId).sorted()

        val result = conversationRef
            .whereEqualTo("participantUserIdList", userLists)
            .limit(1)
            .get().await()

        if (result.isEmpty) {
            Timber.d("No conversation found, creating new conversation")
            firestore.runTransactionWithTimeout { transaction ->
                val newConversationId = conversationRef.document().id
                val newConversation = FirebaseMessageConversationData(
                    conversationId = newConversationId,
                    participantList = listOf(
                        FirebaseParticipantData(userId = userId),
                        FirebaseParticipantData(userId = receiverUserId)
                    ),
                    lastMessageId = "",
                    createdAt = null
                )

                transaction.set(conversationRef.document(newConversationId), newConversation)
                conversationStateFlow.value = newConversation
            }
        } else {
            Timber.d("Conversation found")
            conversationStateFlow.value =
                result.documents.firstOrNull()
                    ?.toObject(FirebaseMessageConversationData::class.java)
        }

    }
}
