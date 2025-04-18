package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationBody

/**
Created by Erdi Özbek
-18.04.2025-
-13:33-
 **/

interface FirebaseCloudOperationRepository {

    suspend fun sendNotification(body: FirebaseCloudNotificationBody): Boolean
}