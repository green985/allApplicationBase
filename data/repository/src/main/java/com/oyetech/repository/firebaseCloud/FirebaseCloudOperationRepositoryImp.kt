package com.oyetech.repository.firebaseCloud

import com.oyetech.domain.repository.firebase.FirebaseCloudOperationRepository
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationBody
import com.oyetech.remote.firebaseCloudRemote.FirebaseCloudApi

/**
Created by Erdi Özbek
-18.04.2025-
-13:31-
 **/

class FirebaseCloudOperationRepositoryImp(private val firebaseCloudApi: FirebaseCloudApi) :
    FirebaseCloudOperationRepository {

    override suspend fun sendNotificationWithPayloadWithDateChange(body: FirebaseCloudNotificationBody): Boolean {
        return firebaseCloudApi.sendNotificationWithPayloadWithDateChange(body).isSuccessful
    }

    override suspend fun sendNotification(body: FirebaseCloudNotificationBody): Boolean {
        return firebaseCloudApi.sendNotificationWithPayload(body).isSuccessful
    }
}