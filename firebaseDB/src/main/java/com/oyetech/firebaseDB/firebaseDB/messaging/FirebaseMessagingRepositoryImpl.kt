package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.repository.firebase.FirebaseCloudOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationBody
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseNotificationTypeEnum
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.IDLE
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.SENT
import com.oyetech.models.firebaseModels.messagingModels.toLocalData
import com.oyetech.models.utils.moshi.serialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date

/**
Created by Erdi Özbek
-16.02.2025-
-17:51-
 **/

@Suppress("LongParameterList")
class FirebaseMessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
    private val firebaseRealtimeHelperRepository: FirebaseRealtimeHelperRepository,
    private val firebaseCloudOperationRepository: FirebaseCloudOperationRepository,
) : FirebaseMessagingRepository {

    private val conversationLimit = 100L
    private val messageLimit = 20L

    private var lastVisibleMessageDocumentCreatedAt: Timestamp? = null

    override fun idlee() {
    }

    override fun initLocalMessageSendOperation(scope: CoroutineScope) {
        // no-op: local message sending queue removed
    }

    override fun getMessageListWithConversationId(conversationId: String): Flow<List<FirebaseMessagingResponseData>> {
        return flow {
            val query = firestore.collection(FirebaseDatabaseKeys.conversations)
                .document(conversationId)
                .collection(FirebaseDatabaseKeys.messages)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(messageLimit)
            val result = query.get().await().documents

            if (result.isNotEmpty()) {
                lastVisibleMessageDocumentCreatedAt = (result.last().get("createdAt") as Timestamp)
            }

            val messageList = result.mapNotNull { doc ->
                val docc = doc.toObject(FirebaseMessagingResponseData::class.java)
                docc?.copy(messageId = doc.id)
            }
            emit(messageList)
        }
    }

    override fun getMessageListWithConversationIdWithMessageId(
        conversationId: String,
    ): Flow<List<FirebaseMessagingResponseData>> {
        return flow {
            val query = firestore.collection(FirebaseDatabaseKeys.conversations)
                .document(conversationId)
                .collection(FirebaseDatabaseKeys.messages)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .startAfter(lastVisibleMessageDocumentCreatedAt)
                .limit(messageLimit)

            val result = query.get().await().documents
            if (result.isNotEmpty()) {
                lastVisibleMessageDocumentCreatedAt = result.last().get("createdAt") as Timestamp
                val messageList = result.mapNotNull { doc ->
                    val docc = doc.toObject(FirebaseMessagingResponseData::class.java)
                    docc?.copy(messageId = doc.id)
                }
                emit(messageList)
            } else {
                Timber.d("No more messages")
                emit(emptyList())
            }
        }
    }

    override suspend fun sendMessage(
        messageText: String,
        conversationId: String,
        receiverUserId: String,
    ) = flow {
        val senderUserId = userRepository.getUserId()
        require(senderUserId.isNotBlank()) { "User not logged in" }
        require(senderUserId != receiverUserId) { "Cannot create conversation with self" }

        val messageLastMessageIdRef = firestore.collection(FirebaseDatabaseKeys.conversations)
            .document(conversationId)

        val conversationRef = firestore.collection(FirebaseDatabaseKeys.conversations)
            .document(conversationId)
            .collection(FirebaseDatabaseKeys.messages).document()

        Timber.d("Sending message document ID =" + conversationRef.id)
        val messageId = conversationRef.id

        val newMessage = FirebaseMessagingResponseData(
            messageId = messageId,
            conversationId = conversationId,
            senderId = senderUserId,
            receiverId = receiverUserId,
            messageText = messageText,
            status = IDLE,
        )

        firebaseRealtimeHelperRepository.sendMessageWithRealtime(newMessage.copy(status = SENT))

        val localMessage = newMessage.toLocalData()
        sendMessageNotification(localMessage)
        emit(newMessage)

        val result = firestore.runTransactionWithTimeout {
            val dbMessage = newMessage.copy(status = SENT)
            it.set(conversationRef, dbMessage)
            it.update(
                messageLastMessageIdRef,
                FirebaseDatabaseKeys.lastMessageId,
                conversationRef.id
            )
            it.update(
                messageLastMessageIdRef,
                FirebaseDatabaseKeys.lastMessageCreatedAt,
                Date()
            )
            dbMessage
        }
        emit(result)
    }

    private suspend fun sendMessageNotification(localMessage: FirebaseMessagingLocalData) {
        try {
            val notificationResult =
                firebaseCloudOperationRepository.sendNotificationWithPayloadWithDateChange(
                    FirebaseCloudNotificationBody(
                        notificationToken =
                            "erJkj6FMQ9-ScqOLRHoEFo:APA91bEiyZccxlUIiuXxs6X9KPGGtqSUKyBFFn_tFwYwJcuCulqdtIkCaUbOwg19Ls_0pvwb25hNiVYODxmzY_hXtHg2--m28tkpEdB2fP4XYizst0mtfBA",
                        payloadData = localMessage.copy(createdAt = 0L, status = SENT).serialize(),
                        notificationType = FirebaseNotificationTypeEnum.Message.toString()
                    )
                )
            Timber.d("Notification result = $notificationResult")
        } catch (e: Exception) {
            Timber.d("Notification error = ${e.message}")
        }
    }

    override suspend fun getConversationDetailOrCreateFlow(receiverUserId: String): Flow<FirebaseMessageConversationData> =
        flow {
            val userId = userRepository.getUserId()

            try {
                require(userId.isNotBlank()) { "User not logged in" }
                require(userId != receiverUserId) { "Cannot create conversation with self" }

                val conversationRef = firestore.collection(FirebaseDatabaseKeys.conversations)

                val userLists = listOf(userId, receiverUserId).sorted()

                val result = conversationRef
                    .whereEqualTo("participantUserIdList", userLists)
                    .limit(1)
                    .get().await()

                if (result.isEmpty) {
                    val userDataList = createUserDataList(userId, receiverUserId)
                    if (userDataList.isEmpty()) {
                        throw GeneralException(LanguageKey.userProfileNotFound)
                    }
                    Timber.d("No conversation found, creating new conversation")
                    val resultt = firestore.runTransactionWithTimeout { transaction ->
                        val newConversationId = conversationRef.document().id
                        val newConversation = FirebaseMessageConversationData(
                            conversationId = newConversationId,
                            participantList = userDataList,
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
                            ?: throw GeneralException(LanguageKey.conversationNotFound)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw GeneralException("getConversationDetailOrCreateFlow error: ${e.message}")
            }
        }

    private suspend fun createUserDataList(
        userId: String,
        conversationReceiverId: String,
    ): List<FirebaseParticipantData> {
        val it = userRepository.getUserProfileModel().value
        if (it.userId.isNotBlank()) {
            val userParticipantDataModel = FirebaseParticipantData(
                userId = it.userId,
                username = it.username,
            )

            val recipientParticipantDataModel =
                userRepository.getUserProfileWithUserId(conversationReceiverId)
                    .firstOrNull()

            if (recipientParticipantDataModel != null) {
                val receiverUserId = recipientParticipantDataModel.userId
                if (receiverUserId.isBlank()) {
                    throw GeneralException(LanguageKey.messageListErrorUserNotFound)
                }
                val receiverUsername = recipientParticipantDataModel.username
                val receiverParticipantDataModel = FirebaseParticipantData(
                    userId = receiverUserId,
                    username = receiverUsername,
                )
                return listOf(userParticipantDataModel, receiverParticipantDataModel)
            }
        } else {
            throw GeneralException(LanguageKey.userIdNotFound)
        }
        return emptyList()
    }

    override fun getConversationList() = flow {
        val userId = userRepository.getUserId()
        require(userId.isNotBlank()) { "User not logged in" }

        try {
            val queryConversation = firestore.collection("conversations")
                .whereArrayContains("participantUserIdList", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(conversationLimit)

            val result = queryConversation.get().await().documents
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

    override fun getConversationListUpdated() = callbackFlow {
        val userId = userRepository.getUserId()
        require(userId.isNotBlank()) { "User not logged in" }

        val queryConversation = firestore.collection("conversations")
            .whereArrayContains("participantUserIdList", userId)
            .whereGreaterThan("lastMessageId", "")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(conversationLimit)

        val listenerRegistration = queryConversation.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e("Error getting conversation list: ${error.message}")
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val conversationList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirebaseMessageConversationData::class.java)
                        ?.copy(conversationId = doc.id)
                }
                Timber.d("Conversation list updated, size: ${conversationList.size}")
                trySend(conversationList)
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }
}
