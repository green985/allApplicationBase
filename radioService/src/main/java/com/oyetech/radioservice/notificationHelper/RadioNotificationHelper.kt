package com.oyetech.radioservice.notificationHelper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.media.app.NotificationCompat.MediaStyle
import com.oyetech.core.contextHelper.getMainActivityStartIntent
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.domain.useCases.GlideOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.PauseReason
import com.oyetech.models.radioProject.radioModels.PlayState
import com.oyetech.models.radioProject.radioModels.PlayState.Idle
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import com.oyetech.models.radioProject.radioModels.PlayState.PrePlaying
import com.oyetech.models.utils.const.NotificationConst
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils.getIntentFlagUpdateWithInMutable
import com.oyetech.radioservice.serviceUtils.ServiceConst
import com.oyetech.radioservice.serviceUtils.ServiceConst.ACTION_RESUME
import com.oyetech.radioservice.services.PlayerService
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-17.11.2022-
-22:40-
 **/

class RadioNotificationHelper(
    private var service: Service,
    private var mediaSessionCompat: MediaSessionCompat,
) {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )

    val glideOperationUseCase: GlideOperationUseCase by KoinJavaComponent.inject(
        GlideOperationUseCase::class.java
    )
    private var lastRadioBitmap: Bitmap? = null

    private var notificationIsActive = false
    private val pauseReason: PauseReason = PauseReason.NONE
    private val lastErrorFromPlayer = -1

    init {
        createNotificationChannel()
        // glideOperationUseCase.setUseCaseContext(context)
    }

    private fun prepareBitmapp(radioLogo: String) {
        glideOperationUseCase.getBitmapWithUrl(radioLogo) {
            lastRadioBitmap = it
        }
    }

    private fun createNotificationChannel() {
        val notificationManager =
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NotificationConst.notificationChannelId,
                NotificationConst.notificationChannelName,
                NotificationManager.IMPORTANCE_LOW
            )

            // Configure the notification channel.
            notificationChannel.description = NotificationConst.notificationChannelDescription
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun fireAndStopDummyNotification() {
        service.apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                // On Android O+ we MUST show notification if started via startForegroundService
                val channel = NotificationChannel(
                    NotificationConst.notificationChannelId,
                    "Temporary", NotificationManager.IMPORTANCE_LOW
                )
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                    channel
                )
                val notification: Notification = Builder(
                    this,
                    NotificationConst.notificationChannelId
                )
                    .setContentTitle("")
                    .setContentText("").build()
                startForeground(
                    NotificationConst.NOTIFY_ID,
                    notification
                )
                stopForeground(true)
            }
        }
    }

    fun sendMessage(
        theTitle: String, theMessage: String, theTicker: String, radioLogo: String,
    ) {
        val itsContext = service as Context
        var theMessagee: String = theMessage ?: ""
        val notificationIntent = itsContext.getMainActivityStartIntent()
        prepareBitmapp(radioLogo)

        notificationIntent.flags = getIntentFlagUpdateWithInMutable()
        val stopIntent =
            Intent(itsContext, PlayerService::class.java)
        stopIntent.action = ServiceConst.ACTION_STOP
        val pendingIntentStop =
            PendingIntent.getService(itsContext, 0, stopIntent, getIntentFlagUpdateWithInMutable())
        val nextIntent =
            Intent(itsContext, PlayerService::class.java)
        nextIntent.action = ServiceConst.ACTION_SKIP_TO_NEXT
        val pendingIntentNext =
            PendingIntent.getService(itsContext, 0, nextIntent, getIntentFlagUpdateWithInMutable())
        val previousIntent =
            Intent(itsContext, PlayerService::class.java)
        previousIntent.action = ServiceConst.ACTION_SKIP_TO_PREVIOUS
        val pendingIntentPrevious = PendingIntent.getService(
            itsContext,
            0,
            previousIntent,
            getIntentFlagUpdateWithInMutable()
        )
        val currentPlayerState: PlayState = getPlayState()
        if ((currentPlayerState === Paused || currentPlayerState === Idle)
            && pauseReason === PauseReason.METERED_CONNECTION
        ) {
            theMessagee = itsContext.getResources()
                .getString(com.oyetech.radioservice.R.string.notify_metered_connection)
        } else if (lastErrorFromPlayer != -1) {
            try {
                theMessagee = itsContext.getResources().getString(lastErrorFromPlayer)
            } catch (ex: NotFoundException) {
                Timber.d("Unknown play error: " + lastErrorFromPlayer + " == " + ex.message)
            }
        }
        val contentIntent = PendingIntent.getActivity(
            itsContext,
            0,
            notificationIntent,
            PlayerServiceUtils.getIntentFlagUpdateWithInMutable()
        )
        val notificationBuilder: Builder = Builder(
            itsContext,
            NotificationConst.notificationChannelId
        )
            .setContentIntent(contentIntent)
            .setContentTitle(theTitle)
            .setContentText(theMessage)
            .setWhen(System.currentTimeMillis())
            .setTicker(theTicker)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(com.oyetech.radioservice.R.drawable.ic_play)
            .setLargeIcon(lastRadioBitmap)
            .addAction(
                com.oyetech.radioservice.R.drawable.ic_stop,
                itsContext.getString(com.oyetech.radioservice.R.string.action_stop),
                pendingIntentStop
            )
            .addAction(
                com.oyetech.radioservice.R.drawable.ic_skip_previous_24dp,
                itsContext.getString(com.oyetech.radioservice.R.string.action_skip_to_previous),
                pendingIntentPrevious
            )
        if (currentPlayerState === Playing || currentPlayerState === PrePlaying) {
            val pauseIntent = Intent(
                itsContext,
                PlayerService::class.java
            )
            pauseIntent.action = ServiceConst.ACTION_PAUSE
            val pendingIntentPause = PendingIntent.getService(
                itsContext,
                0,
                pauseIntent,
                getIntentFlagUpdateWithInMutable()
            )
            notificationBuilder.addAction(
                com.oyetech.radioservice.R.drawable.ic_pause,
                itsContext.getString(com.oyetech.radioservice.R.string.action_pause),
                pendingIntentPause
            )
            notificationBuilder.setUsesChronometer(true)
                .setOngoing(true)
        } else if (currentPlayerState === Paused || currentPlayerState === Idle) {
            val resumeIntent = Intent(
                itsContext,
                PlayerService::class.java
            )
            resumeIntent.action = ACTION_RESUME
            val pendingIntentResume = PendingIntent.getService(
                itsContext,
                0,
                resumeIntent,
                getIntentFlagUpdateWithInMutable()
            )
            notificationBuilder.addAction(
                com.oyetech.radioservice.R.drawable.ic_play,
                itsContext.getString(com.oyetech.radioservice.R.string.action_resume),
                pendingIntentResume
            )
            notificationBuilder.setUsesChronometer(false)
                .setDeleteIntent(pendingIntentStop)
                .setOngoing(false)
        }
        notificationBuilder.addAction(
            com.oyetech.radioservice.R.drawable.ic_skip_next_24dp,
            itsContext.getString(com.oyetech.radioservice.R.string.action_skip_to_next),
            pendingIntentNext
        )

            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(1, 2, 3 /* previous, play/pause, next */)
                    .setCancelButtonIntent(pendingIntentStop)
                    .setShowCancelButton(true)

            )

        val notification = notificationBuilder.build()
        doInTryCatch {
            service.startForeground(
                NotificationConst.NOTIFY_ID,
                notification
            )
        }
        notificationIsActive = true
        if (currentPlayerState === Paused || currentPlayerState === Idle) {
            service.stopForeground(false) // necessary to make notification dismissible
        }
    }

    private fun getPlayState(): PlayState {
        return radioOperationUseCase.getPlayerState()
    }

}
