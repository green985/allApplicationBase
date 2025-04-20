package com.oyetech.notificationmodule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.oyetech.models.firebaseModels.cloudFunction.FirebaseNotificationType

/**
Created by Erdi Özbek
-18.04.2025-
-14:06-
 **/

class AppNotificationOperator(private val context: Context) {

    val notificationIdList = mutableListOf<Int>()

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        title: String,
        message: String,
        channelId: String = "default_channel_id",
        notificationType: FirebaseNotificationType = FirebaseNotificationType.Message,
        notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    ) {
        val pendingIntent = getPendingIntent()

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        builder.setSmallIconWithNotificationType(notificationType)

        createNotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationIdList.add(notificationId)
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }

    fun getPendingIntent(): PendingIntent {
        val requestCode = 0
        val className =
            "com.oyetech.composebase.projectQuotesFeature.main.QuoteMainActivity"
        val activityClass = Class.forName(className)

        val intent = Intent(context, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent
    }

}

private fun Builder.setSmallIconWithNotificationType(notificationType: FirebaseNotificationType) {
    when (notificationType) {
        FirebaseNotificationType.Message -> {
            setSmallIcon(R.drawable.ic_notification_message_icon)
        }
    }
}
