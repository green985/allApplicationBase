package com.oyetech.models.firebaseModels.cloudFunction

import androidx.annotation.Keep

/**
Created by Erdi Özbek
-18.04.2025-
-13:27-
 **/

@Keep
data class FirebaseCloudNotificationBody(
    val notificationToken: String,
    val payloadData: String,
    val notificationType: String,
)

@Keep
enum class FirebaseNotificationType {
    //    DEFAULT,
    Message,  // firebaseMessagingLocalData
//    CALL,
//    OTHER
}