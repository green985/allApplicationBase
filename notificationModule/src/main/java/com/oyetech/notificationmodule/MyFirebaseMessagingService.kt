package com.oyetech.notificationmodule

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val appNotificationOperator: AppNotificationOperator by KoinJavaComponent.inject(
        AppNotificationOperator::class.java
    )
    val notificationHandlerHelper: NotificationHandlerHelper by KoinJavaComponent.inject(
        NotificationHandlerHelper::class.java
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")

            notificationHandlerHelper.handle(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
    }
}
