package com.oyetech.firebaserealtime.di

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.domain.repository.messaging.local.MessagesAllLocalDataSourceRepository
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingResponseData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus
import com.oyetech.models.firebaseModels.messagingModels.toLocalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-4.03.2025-
-20:09-
 **/

class FirebaseRealtimeHelperRepositoryImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val messagesAllLocalDataSourceRepository: MessagesAllLocalDataSourceRepository,
) : FirebaseRealtimeHelperRepository {

    override val realtimeMessageSendOperationResultState =
        MutableStateFlow<FirebaseMessagingResponseData?>(null)

    init {
        Timber.d("FirebaseRealtimeHelperRepository created")
    }

    override fun sendMessageWithRealtime(messageBody: FirebaseMessagingResponseData) {

        try {
            val postData = messageBody.copy(timestamp = ServerValue.TIMESTAMP)
            val myRef = getUserRef(messageBody.receiverId)
            if (myRef == null) {
                Timber.d("User ref is null")

            } else {
                myRef.child(FirebaseDatabaseKeys.messages).push()
                    .setValue(postData)
                    .addOnCompleteListener {
                        realtimeMessageSendOperationResultState.tryEmit(messageBody)
                    }.addOnFailureListener {
                        realtimeMessageSendOperationResultState.tryEmit(
                            messageBody.copy(status = MessageStatus.ERROR)
                        )
                    }

            }
        } catch (e: Exception) {
            throw GeneralException(e)
        }
    }

    override suspend fun observeUserMessagesRealtimeOperations(): Flow<Unit> {
        return flow<Unit> {
            try {
                firebaseUserRepository.getUserProfileModel().collectLatest {
                    val userId = it?.userId ?: ""

                    if (userId.isBlank()) {
                        throw GeneralException("User id is null or blank")
                    }
                    val myRef = getUserRef()

                    val lastMessage = messagesAllLocalDataSourceRepository.getLastMessage(userId)
                    val lastMessageTimestamp = lastMessage?.createdAt ?: 0L

                    myRef?.child(FirebaseDatabaseKeys.messages)
                        ?.orderByChild("timestamp")
                        ?.startAt(lastMessageTimestamp.toDouble())
                        ?.addChildEventListener(object :
                            ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?,
                            ) {
                                val message =
                                    snapshot.getValue(FirebaseMessagingResponseData::class.java)

                                saveMessageToLocal(message)

                                Timber.d("onChildAdded: ${message?.messageText}")
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?,
                            ) {
                                val message =
                                    snapshot.getValue(FirebaseMessagingResponseData::class.java)
                                saveMessageToLocal(message)
                                Timber.d("onChildChanged: ${message?.messageText}")
                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                val message =
                                    snapshot.getValue(FirebaseMessagingResponseData::class.java)
                                Timber.d("onChildRemoved: ${message?.messageText}")
                            }

                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?,
                            ) {
                                val message =
                                    snapshot.getValue(FirebaseMessagingResponseData::class.java)
                                Timber.d("onChildMoved: ${message?.messageText}")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Timber.d("onCancelled: ${error.message}")
                            }
                        })
                }

            } catch (e: Exception) {
                Timber.d("observeUserMessagesRealtimeOperations error: ${e.message}")
                throw GeneralException(e)
            }
        }

    }

    private fun saveMessageToLocal(message: FirebaseMessagingResponseData?) {
        val localMessageData = message?.toLocalData()

        if (localMessageData?.messageId?.isNotBlank() == true) {
            GlobalScope.launch(Dispatchers.IO) {
                messagesAllLocalDataSourceRepository.insertMessage(
                    localMessageData
                )
            }
        } else {
            // todo analytics nasil olur aq eventi ekle
            Timber.d("messageId null do nothing")
        }
    }

    private fun getUserRef(userSelection: String? = null): DatabaseReference? {
        val userId = userSelection?.takeIf { it.isNotBlank() } ?: firebaseUserRepository.getUserId()

        if (userId.isBlank()) {
            Timber.d("User id is blank")
            return null
        }

        return firebaseDatabase.getReference(createUserPath(userId))
    }

    private fun createUserPath(userId: String): String {
        return "users/$userId"
    }
}
