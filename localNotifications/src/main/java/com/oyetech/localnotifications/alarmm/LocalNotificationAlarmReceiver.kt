package com.oyetech.localnotifications.alarmm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.oyetech.corecommon.contextHelper.getAppName
import com.oyetech.corecommon.contextHelper.getIntentFlagUpdateWithInMutable
import com.oyetech.corecommon.contextHelper.getMainActivityStartIntent
import com.oyetech.cripto.activityArgs.AlarmBundleKey
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.localnotifications.R
import com.oyetech.models.entity.bibleProperties.BibleVersePropertyData
import com.oyetech.models.utils.const.NotificationConst
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-20.04.2023-
-17:27-
 **/

class LocalNotificationAlarmReceiver() : BroadcastReceiver() {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val sharedHelperRepository: SharedHelperRepository by KoinJavaComponent.inject(
        SharedHelperRepository::class.java
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("alarmramksdnaksndka sn dkasnd kansdk  na")
        var alarmId = intent?.getIntExtra(AlarmBundleKey.LOCAL_NOTIFICATION_ALARM_ID, -1)

        if (alarmId == -1) {
            Timber.d("some problem....")
            return
        }

        sendNotification(intent)
    }

    fun sendNotification(intent: Intent?) {
        if (!canShowNotification(intent)) {
            return
        }

        var appName = context.getAppName() ?: ""
        var message = getNotificationText(intent)

        if (message.isNullOrEmpty()) {
            Timber.d("notification message cannot be empty...")
            return
        }

        val itsContext = context
        val notificationIntent = itsContext.getMainActivityStartIntent()

        var notificationNotifId = if (controlNotificationIsPray(intent)) {
            NotificationConst.PRAY_NOTIFY_ID
        } else {
            NotificationConst.VERSE_NOTIFY_ID
        }

        var notificationId = if (controlNotificationIsPray(intent)) {
            NotificationConst.localPrayNotificationNotificationId
        } else {
            NotificationConst.localVerseNotificationNotificationId
        }

        var channelId = if (controlNotificationIsPray(intent)) {
            NotificationConst.localPrayNotificationChannelId
        } else {
            NotificationConst.localVerseNotificationChannelId
        }

        notificationIntent.flags = getIntentFlagUpdateWithInMutable()
        val contentIntent = PendingIntent.getActivity(
            itsContext,
            0,
            notificationIntent,
            getIntentFlagUpdateWithInMutable()
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationId,
                channelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = NotificationConst.notificationChannelDescription

            channel.enableLights(true)
            channel.lightColor = Color.YELLOW
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder: Builder = Builder(
            itsContext,
            notificationId
        )
            .setContentIntent(contentIntent)
            .setContentTitle(appName)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setWhen(System.currentTimeMillis())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_tab_all_radio)

        val notification = notificationBuilder.build()


        notificationManager.notify(
            notificationNotifId,
            notification
        )
    }

    private fun getNotificationText(intent: Intent?): String {
        var prayData = intent?.getStringExtra(AlarmBundleKey.LOCAL_NOTIFICATION_PRAY_DATA)
        if (prayData?.isNotBlank() == true) {
            sharedHelperRepository.putLastShownAlarmPray(prayData)
            return prayData
        }
        var verseData = intent?.getParcelableExtra<BibleVersePropertyData>(
            AlarmBundleKey.LOCAL_NOTIFICATION_VERSE_DATA
        )

        if (verseData != null) {
            sharedHelperRepository.putLastShownAlarmVerse(verseData)
            return verseData.content ?: ""
        }

        return ""

    }

    private fun controlNotificationIsPray(intent: Intent?): Boolean {
        var prayData = intent?.getStringExtra(AlarmBundleKey.LOCAL_NOTIFICATION_PRAY_DATA)
        if (prayData?.isNotBlank() == true) {
            return true
        }

        return false
    }

    private fun canShowNotification(intent: Intent?): Boolean {
        var isPrayNotification = controlNotificationIsPray(intent)

        var notificationSettingsData = sharedHelperRepository.getNotificationSettingsData()

        Timber.d("notification settings Data = =" + notificationSettingsData.toString())

        if (isPrayNotification && notificationSettingsData.prayerOfTheDay) {
            return true
        }

        if (!isPrayNotification && notificationSettingsData.verseOfTheDay) {
            return true
        }

        return false

    }


}

