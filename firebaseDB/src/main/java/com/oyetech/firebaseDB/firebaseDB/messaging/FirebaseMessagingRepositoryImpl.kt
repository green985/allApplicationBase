package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.firebase.FirebaseMessagingLocalRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.IDLE
import com.oyetech.models.firebaseModels.messagingModels.toLocalData
import com.oyetech.models.firebaseModels.messagingModels.toRemoteData
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    private val firebaseMessagingLocalRepository: FirebaseMessagingLocalRepository,
    private val dispatcher: AppDispatchers,
    private val activityProviderUseCase: ActivityProviderUseCase,
) : FirebaseMessagingRepository {

    override val userMessageConversationList =
        MutableStateFlow<List<FirebaseMessageConversationData>?>(null)

    private val sendingDelay = 100L
    private val newMessageToSendOperationDelay = 25L

    var sendingOperationJob: Job? = null

    override fun idlee() {

    }

    init {

    }

    override fun initLocalMessageSendOperation(scope: CoroutineScope) {
        GlobalScope.launch {

            activityProviderUseCase.activityOnResumeMutableStateFlow.collectLatest {
                Timber.d("activityOnResumeMutableStateFlow: $it")
                sendingOperationJob?.cancel()
                if (it == true) {
                    sendingOperationJob = observeAndSendMessageSingle(scope)
                }

            }
        }
    }

    fun observeAndSendMessageSingle(scope: CoroutineScope): Job {
        return scope.launch(dispatcher.io) {
            firebaseMessagingLocalRepository.firstInMessageFlow().onEach {
                Timber.d("observeAndSendMessageSingle: message sending start == " + it?.messageText)
                delay(newMessageToSendOperationDelay)
                sendMessageWithLocalTrigger(it)
            }.asResult().collectLatest {
                Timber.d("observeAndSendMessageSingle: finish")
            }

        }

    }

    private suspend fun FirebaseMessagingRepositoryImpl.sendMessageWithLocalTrigger(
        it: FirebaseMessagingLocalData?,
    ) {
        if (it == null) {
            return
        }

        try {
            delay(sendingDelay)
            if (it == null) {
                Timber.d("observeAndSendMessageSingle: null ama nasil aq :D")
                throw Exception("Message is null")
            } else {
                val result =
                    sendMessageWithTry(it.messageId).firstOrNull()
                if (result != null) {
                    firebaseMessagingLocalRepository.deleteMessageFromLocal(it.messageId)
                    it
                } else {
                    throw Exception("Message send error")
                }
            }
        } catch (e: Exception) {
            Timber.d("error send again ")
            delay(sendingDelay)
            sendMessageWithLocalTrigger(it)
        }
    }

    private suspend fun sendMessageWithTry(messageId: String): Flow<FirebaseMessagingResponseData> {
        val foundedMessage = firebaseMessagingLocalRepository.getMessage(messageId)

        if (foundedMessage != null) {
            val messageBody = foundedMessage.toRemoteData()
            val sendMessageResult = sendMessageWithBody(messageBody)
            return flow {
                emit(sendMessageResult)
            }
        } else {
            throw Exception("Message not found")
        }

    }

    private suspend fun sendMessageWithBody(messageBody: FirebaseMessagingResponseData): FirebaseMessagingResponseData {
        return try {
            val conversationRef =
                firestore.collection(FirebaseDatabaseKeys.conversations)
                    .document(messageBody.conversationId)
                    .collection(FirebaseDatabaseKeys.messages).document()
            val result = firestore.runTransactionWithTimeout {
                it.set(conversationRef, messageBody)
                messageBody
            }
            return result
        } catch (e: Exception) {
            throw Exception("Message send error")
        }
    }

    override suspend fun sendMessage(
        messageText: String,
        conversationId: String,
        receiverUserId: String,
    ) =
        flow {
//            val userId = userRepository.getUserId()

            val senderUserId = "denemeFirstUser"
            var localMessage = FirebaseMessagingLocalData()
            require(senderUserId.isNotBlank()) { "User not logged in" }
            require(senderUserId != receiverUserId) { "Cannot create conversation with self" }

            try {

                val conversationRef =
                    firestore.collection(FirebaseDatabaseKeys.conversations)
                        .document(conversationId)
                        .collection(FirebaseDatabaseKeys.messages).document()

                Timber.d("Sending message document ID =" + conversationRef.id)
                val newMessage = FirebaseMessagingResponseData(
                    messageId = conversationRef.id,
                    conversationId = conversationId,
                    senderId = senderUserId,
                    receiverId = receiverUserId,
                    messageText = messageText,
                    status = IDLE,
                    createdAt = null
                )

                localMessage = newMessage.toLocalData()
                val result = firestore.runTransactionWithTimeout {
                    it.set(conversationRef, newMessage)
                    newMessage
                }

                emit(result)
            } catch (e: Exception) {
                firebaseMessagingLocalRepository.insertMessage(localMessage)
                Timber.d("message send error = " + ErrorMessage.fetchErrorMessage(e.message))
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
