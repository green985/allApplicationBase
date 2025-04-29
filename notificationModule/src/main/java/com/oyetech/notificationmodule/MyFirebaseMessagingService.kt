package com.oyetech.notificationmodule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.tools.contextHelper.getApplicationLogo
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val appNotificationOperator: AppNotificationOperator by KoinJavaComponent.inject(
        AppNotificationOperator::class.java
    )
    val messagesAllOperationRepository: MessagesAllOperationRepository by KoinJavaComponent.inject(
        MessagesAllOperationRepository::class.java
    )

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")
            handleNotification(remoteMessage.data.get("payloadData"))
        }
    }

    override fun onNewToken(token: String) {
        // todo maybe save token to server
    }

    private fun sendNotification(messageBody: String) {
        val requestCode = 0
        val className =
            "com.oyetech.composebase.projectQuotesFeature.main.QuoteMainActivity"
        val activityClass = Class.forName(className)

        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bitmap = getApplicationLogo()?.toBitmap()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("deneme")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if (bitmap != null) {
            notificationBuilder.setSmallIcon(IconCompat.createWithBitmap(bitmap))
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}