package com.oyetech.notificationmodule

import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationTypeWrapper
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseNotificationTypeEnum
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.utils.moshi.deserialize
import timber.log.Timber

/**
Created by Erdi Özbek
-18.04.2025-
-14:15-
 **/

fun MyFirebaseMessagingService.handleNotification(notificationBody: String?) {
    if (notificationBody.isNullOrBlank()) {
        Timber.d("Notification body is null or blank")
        return
    }

    try {
        val firebaseCloudNotificationTypeWrapper =
            notificationBody.deserialize<FirebaseCloudNotificationTypeWrapper>()


        when (firebaseCloudNotificationTypeWrapper?.notificationType) {
            FirebaseNotificationTypeEnum.Message.name -> {
                Timber.d("Message notification type")
                // Handle message notification
                val messageNotificationBody =
                    notificationBody.deserialize<FirebaseMessagingLocalData>()
                if (messageNotificationBody != null) {
                    messagesAllOperationRepository.insertMessageWithGlobalScope(
                        messageNotificationBody
                    )
                    if (messagesAllOperationRepository.currentConversationId.value ==
                        messageNotificationBody.conversationId
                    ) {
                        Timber.d("Current conversation id matches")
                        return
                    }
                    appNotificationOperator.showNotification(
                        messageNotificationBody?.senderId ?: " sender bos",
                        messageNotificationBody?.messageText ?: "message bos "
                    )
                } else {
                    Timber.d("messageNotificationBody is null")
                }
            }

            else -> {
                Timber.d("Unknown notification type")
            }
        }


    } catch (e: Exception) {
        e.printStackTrace()
    }
}