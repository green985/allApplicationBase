package com.oyetech.repository.firebaseMessaging

import com.oyetech.dao.firebaseMessaging.FirebaseMessagingDao
import com.oyetech.domain.repository.firebase.FirebaseMessagingLocalRepository
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-20.02.2025-
-22:13-
 **/

class FirebaseMessagingLocalRepositoryImp(
    private val firebaseMessagingDao: FirebaseMessagingDao,
) : FirebaseMessagingLocalRepository {

    override suspend fun insertMessage(message: FirebaseMessagingLocalData) {
        firebaseMessagingDao.insert(message)
    }

    override suspend fun getMessage(messageId: String): FirebaseMessagingLocalData? {
        return firebaseMessagingDao.getMessageWithId(messageId)
    }

    // it will call when user successfully sent message or delete operation
    override fun deleteMessageFromLocal(vararg messageId: String) {
        firebaseMessagingDao.deleteLastList(messageId.toList())
    }

    override fun observeSendingMessages(): Flow<List<FirebaseMessagingLocalData>?> {
        return firebaseMessagingDao.getMessageListFlow()
    }

    override fun firstInMessageFlow(): Flow<FirebaseMessagingLocalData?> {
        return firebaseMessagingDao.getFirstInMessage()
    }


}
