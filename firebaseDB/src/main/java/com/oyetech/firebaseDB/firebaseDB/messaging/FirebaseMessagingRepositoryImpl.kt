package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.IDLE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date

/**
Created by Erdi Özbek
-16.02.2025-
-17:51-
 **/

class FirebaseMessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) : FirebaseMessagingRepository {

    override val userMessageConversationList =
        MutableStateFlow<List<FirebaseMessageConversationData>?>(null)

    override fun idlee() {

    }

    override suspend fun sendMessage(
        messageText: String,
        conversationId: String,
        receiverUserId: String,
    ) =
        flow<FirebaseMessagingResponseData> {
            val senderUserId = "denemeFirstUser"
//            val userId = userRepository.getUserId()
            try {

                require(senderUserId.isNotBlank()) { "User not logged in" }
                require(senderUserId != receiverUserId) { "Cannot create conversation with self" }

                val conversationRef =
                    firestore.collection(FirebaseDatabaseKeys.conversations)
                        .document(conversationId)
                        .collection(FirebaseDatabaseKeys.messages).document()

                Timber.d("Sending message document ID =" + conversationRef.id)

                val result = firestore.runTransactionWithTimeout {
                    val newMessage = FirebaseMessagingResponseData(
                        messageId = conversationRef.id,
                        conversationId = conversationId,
                        senderId = senderUserId,
                        receiverId = receiverUserId,
                        messageText = messageText,
                        status = IDLE,
                        createdAt = null
                    )
                    it.set(conversationRef, newMessage)
                    newMessage
                }
                emit(result)
            } catch (e: Exception) {
                Timber.d(e)
                throw e
            }
        }

    override suspend fun getConversationDetailOrCreateFlow(receiverUserId: String): Flow<FirebaseMessageConversationData> =
        flow {
//            val userId = userRepository.getUserId()

            try {

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
                    val resultt = firestore.runTransactionWithTimeout { transaction ->
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

                        transaction.set(
                            conversationRef.document(newConversationId),
                            newConversation
                        )
                        newConversation.copy(createdAt = Date())
                    }
                    emit(resultt)
                } else {
                    Timber.d("Conversation found")
                    emit(
                        result.documents.firstOrNull()
                            ?.toObject(FirebaseMessageConversationData::class.java)
                            ?: throw Exception(LanguageKey.conversationNotFound)
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }

        }

    override suspend fun getConversationList() = flow {
//            val userId = userRepository.getUserId()
        val userId = "denemeFirstUser"
        require(userId.isNotBlank()) { "User not logged in" }

        try {
            val query = firestore.collection("conversations")
                .whereArrayContains("participantUserIdList", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
//                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            // todo will be fix after connection between messages and conversation

            val result = query.get().await().documents
            val conversationList = result.mapNotNull { doc ->
                doc.toObject(FirebaseMessageConversationData::class.java)
                    ?.copy(conversationId = doc.id)
            }
            emit(conversationList)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
