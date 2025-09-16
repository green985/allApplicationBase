package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class DigitalNoteNotificationFactory {
    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            DigitalNoteNotificationIds.CHANNEL_ID,
            DigitalNoteNotificationIds.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager.createNotificationChannel(channel)
    }

    fun buildForegroundNotification(
        context: Context,
        contentText: String,
        mainActivityClass: Class<out Activity>,
    ): Notification {
        val intent = Intent(context, mainActivityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            DigitalNoteNotificationIds.REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = Notification.Builder(context, DigitalNoteNotificationIds.CHANNEL_ID)
            .setContentTitle("Digital Notes")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
        return builder.build()
    }
}
