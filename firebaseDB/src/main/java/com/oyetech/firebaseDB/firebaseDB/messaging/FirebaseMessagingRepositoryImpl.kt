package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.firebase.FirebaseCloudOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.domain.repository.messaging.MessagesSendingOperationRepository
import com.oyetech.domain.repository.messaging.local.MessagesAllLocalDataSourceRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationBody
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessageConversationData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.FirebaseParticipantData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.IDLE
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.SENT
import com.oyetech.models.firebaseModels.messagingModels.toLocalData
import com.oyetech.models.firebaseModels.messagingModels.toRemoteData
import com.oyetech.models.utils.moshi.serialize
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

@Suppress("LongParameterList")
class FirebaseMessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
    private val messagesSendingOperationRepository: MessagesSendingOperationRepository,
    private val messagesAllOperationRepository: MessagesAllLocalDataSourceRepository,
    private val dispatcher: AppDispatchers,
    private val activityProviderUseCase: ActivityProviderUseCase,
    private val firebaseRealtimeHelperRepository: FirebaseRealtimeHelperRepository,
    private val firebaseCloudOperationRepository: FirebaseCloudOperationRepository,
) : FirebaseMessagingRepository {

    private val conversationLimit = 100L
    private val messageLimit = 20L
    private val sendingDelay = 100L
    private val newMessageToSendOperationDelay = 25L

    private var lastVisibleMessageDocumentCreatedAt: Timestamp? = null

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

    override fun getMessageListWithConversationId(conversationId: String): Flow<List<FirebaseMessagingResponseData>> {
        return flow {
            val query = firestore.collection(FirebaseDatabaseKeys.conversations)
                .document(conversationId)
                .collection(FirebaseDatabaseKeys.messages)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(messageLimit)
            val result = query.get().await().documents

            lastVisibleMessageDocumentCreatedAt = (result.last().get("createdAt") as Timestamp)

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
            delay(1500)
            val createdAt =
                messagesAllOperationRepository.getLastMessageWithConversationId(conversationId)?.createdAt
                    ?: 0L
            if (createdAt == 0L) {
                emit(emptyList())
            } else {

                val query = firestore.collection(FirebaseDatabaseKeys.conversations)
                    .document(conversationId)
                    .collection(FirebaseDatabaseKeys.messages)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .startAfter(lastVisibleMessageDocumentCreatedAt)
                    .limit(messageLimit)

                val result = query.get().await().documents
                lastVisibleMessageDocumentCreatedAt = result.last().get("createdAt") as Timestamp

                val messageList = result.mapNotNull { doc ->
                    val docc = doc.toObject(FirebaseMessagingResponseData::class.java)
                    docc?.copy(messageId = doc.id)
                }
//                Timber.d("getMessageListWithConversationId: ${messageList.toString()}")
                emit(messageList)
            }
        }
    }

    fun observeAndSendMessageSingle(scope: CoroutineScope): Job {
        return scope.launch(dispatcher.io) {
            messagesSendingOperationRepository.firstInMessageFlow().onEach {
                Timber.d("observeAndSendMessageSingle: message sending start == " + it?.messageText)
                delay(newMessageToSendOperationDelay)
                sendMessageWithLocalTrigger(it)
            }.asResult().collectLatest {
                Timber.d("observeAndSendMessageSingle: finish")
            }
        }
    }

    private suspend fun sendMessageWithLocalTrigger(
        it: FirebaseMessagingLocalData?,
    ) {
        if (it == null) {
            return
        }

        try {
            delay(sendingDelay)
            val result =
                sendMessageWithTry(it.messageId).firstOrNull()
            if (result != null) {
                messagesSendingOperationRepository.deleteMessageFromLocal(it.messageId)
                messagesAllOperationRepository.insertMessage(
                    result.toLocalData().copy(status = SENT)
                )
                it
            } else {
                throw GeneralException("Message send error")
            }
        } catch (e: Exception) {
            // todo will be add trigger canceller
            Timber.d("error send again ")
            delay(sendingDelay)
            sendMessageWithLocalTrigger(it)
        }
    }

    private suspend fun sendMessageWithTry(messageId: String): Flow<FirebaseMessagingResponseData> {
        val foundedMessage =
            messagesSendingOperationRepository.getSendingMessageWithMessageId(messageId)

        if (foundedMessage != null) {
            val messageBody = foundedMessage.toRemoteData()
            val sendMessageResult = sendMessageWithBody(messageBody)
            return flow {
                emit(sendMessageResult)
            }
        } else {
            throw GeneralException("Message not found")
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
            throw GeneralException("Message send error")
        }
    }

    override suspend fun sendMessage(
        messageText: String,
        conversationId: String,
        receiverUserId: String,
    ) =
        flow {
            val senderUserId = userRepository.getUserId()
            var localMessage = FirebaseMessagingLocalData()
            require(senderUserId.isNotBlank()) { "User not logged in" }
            require(senderUserId != receiverUserId) { "Cannot create conversation with self" }

            try {

                val messageLastMessageIdRef =
                    firestore.collection(FirebaseDatabaseKeys.conversations)
                        .document(conversationId)

                val conversationRef =
                    firestore.collection(FirebaseDatabaseKeys.conversations)
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

                localMessage = newMessage.toLocalData()

                sendNotification(localMessage)


                messagesAllOperationRepository.insertMessage(localMessage)
                emit(newMessage)

                val result = firestore.runTransactionWithTimeout {
                    val dbMessage = newMessage.copy(status = SENT)
                    it.set(conversationRef, dbMessage)
                    it.update(
                        messageLastMessageIdRef,
                        FirebaseDatabaseKeys.lastMessageId,
                        conversationRef.id
                    )
                    dbMessage
                }
//                Timber.d("Sending message document ID result =" + result.messageId)

                emit(result)
                messagesAllOperationRepository.insertMessage(localMessage.copy(status = SENT))

            } catch (e: Exception) {
                messagesSendingOperationRepository.insertSendingMessage(localMessage)
                messagesAllOperationRepository.insertMessage(localMessage.copy(status = MessageStatus.ERROR))

                Timber.d("message send error = " + ErrorMessage.fetchErrorMessage(e.message))
                throw e
            }
        }

    private suspend fun sendNotification(localMessage: FirebaseMessagingLocalData) {
        try {
            val notificationResult = firebaseCloudOperationRepository.sendNotification(
                FirebaseCloudNotificationBody(
                    notificationToken = "dCfaNx8nQQyV4S65l0iYmm:APA91bH_QTlKH4p5DwnDZYG8VEOLVLSR8nPRdiQBgzd_ElpOIJYWvKoszJqEIwGIxHFQTkdsfHguPhmSf-BDdbryfPWKsG6qQF0-9pxRmoH4grtTeE3M97I",
                    payloadData = localMessage.serialize()
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

            // todo will be add receiver property

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
                            ?: throw GeneralException(LanguageKey.conversationNotFound)
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }

        }

    override fun getConversationList() = flow {
        val userId = userRepository.getUserId() //
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


}
