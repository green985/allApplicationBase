package com.oyetech.audioPlayerHelper.notificationHelper

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
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.media.app.NotificationCompat.MediaStyle
import com.oyetech.core.contextHelper.getMainActivityStartIntent
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.domain.useCases.GlideOperationUseCase
import com.oyetech.domain.useCases.contentOperations.ContentOperationUseCase
import com.oyetech.models.radioModels.PauseReason
import com.oyetech.models.radioModels.PlayState
import com.oyetech.models.radioModels.PlayState.Idle
import com.oyetech.models.radioModels.PlayState.Paused
import com.oyetech.models.radioModels.PlayState.Playing
import com.oyetech.models.radioModels.PlayState.PrePlaying
import com.oyetech.models.utils.const.NotificationConst
import com.oyetech.audioPlayerHelper.serviceUtils.PlayerServiceUtils
import com.oyetech.audioPlayerHelper.serviceUtils.PlayerServiceUtils.getIntentFlagUpdateWithInMutable
import com.oyetech.audioPlayerHelper.serviceUtils.ServiceConst
import com.oyetech.audioPlayerHelper.serviceUtils.ServiceConst.ACTION_RESUME
import com.oyetech.audioPlayerHelper.services.PlayerService
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

    val contentOperationUseCase: ContentOperationUseCase by KoinJavaComponent.inject(
        ContentOperationUseCase::class.java
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
        glideOperationUseCase.setUseCaseContext(context)
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
        theTitle: String,
        theMessage: String,
        theTicker: String,
        radioLogo: String,
    ) {
        // Get the Context from the service
        val itsContext = service as Context

        // Initialize the message and notification intent
        var theMessagee: String = theMessage ?: ""
        val notificationIntent = itsContext.getMainActivityStartIntent()

        // Prepare the bitmap (assuming it's defined in the prepareBitmapp function)
        prepareBitmapp(radioLogo)

        // Set flags for the notification intent
        notificationIntent.flags = getIntentFlagUpdateWithInMutable()

        // Create intents for different actions
        val stopIntent = Intent(itsContext, PlayerService::class.java)
        stopIntent.action = ServiceConst.ACTION_STOP

        val nextIntent = Intent(itsContext, PlayerService::class.java)
        nextIntent.action = ServiceConst.ACTION_SKIP_TO_NEXT

        val previousIntent = Intent(itsContext, PlayerService::class.java)
        previousIntent.action = ServiceConst.ACTION_SKIP_TO_PREVIOUS

        // Create pending intents for the actions
        val pendingIntentStop =
            PendingIntent.getService(itsContext, 0, stopIntent, getIntentFlagUpdateWithInMutable())
        val pendingIntentNext =
            PendingIntent.getService(itsContext, 0, nextIntent, getIntentFlagUpdateWithInMutable())
        val pendingIntentPrevious = PendingIntent.getService(
            itsContext,
            0,
            previousIntent,
            getIntentFlagUpdateWithInMutable()
        )

        // Get the current player state and handle message updates
        val currentPlayerState: PlayState = getPlayState()
        if ((currentPlayerState === Paused || currentPlayerState === Idle) && pauseReason === PauseReason.METERED_CONNECTION) {
            theMessagee = itsContext.getResources()
                .getString(com.oyetech.audioPlayerHelper.R.string.notify_metered_connection)
        } else if (lastErrorFromPlayer != -1) {
            try {
                theMessagee = itsContext.getResources().getString(lastErrorFromPlayer)
            } catch (ex: NotFoundException) {
                Timber.d("Unknown play error: $lastErrorFromPlayer == ${ex.message}")
            }
        }

        // Create content intent for the notification
        val contentIntent = PendingIntent.getActivity(
            itsContext,
            0,
            notificationIntent,
            PlayerServiceUtils.getIntentFlagUpdateWithInMutable()
        )

        // Create the notification builder
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            itsContext, NotificationConst.notificationChannelId
        )

        // Configure notification details
        notificationBuilder.setContentIntent(contentIntent)
        notificationBuilder.setContentTitle(theTitle)
        notificationBuilder.setContentText(theMessage)
        notificationBuilder.setWhen(System.currentTimeMillis())
        notificationBuilder.setTicker(theTicker)
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationBuilder.setSmallIcon(com.oyetech.audioPlayerHelper.R.drawable.ic_play)
        notificationBuilder.setLargeIcon(lastRadioBitmap)

        // Add action buttons to the notification
        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_cancel,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_stop),
            pendingIntentStop
        )

        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_skip_previous_24dp,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_previous),
            pendingIntentPrevious
        )

        if (currentPlayerState === Playing || currentPlayerState === PrePlaying) {
            // Create a pause action and set notification as ongoing
            val pauseIntent = Intent(itsContext, PlayerService::class.java)
            pauseIntent.action = ServiceConst.ACTION_PAUSE
            val pendingIntentPause = PendingIntent.getService(
                itsContext,
                0,
                pauseIntent,
                getIntentFlagUpdateWithInMutable()
            )

            notificationBuilder.addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_check,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_pause),
                pendingIntentPause
            )
            notificationBuilder.setUsesChronometer(true)
            notificationBuilder.setOngoing(true)
        } else if (currentPlayerState === Paused || currentPlayerState === Idle) {
            // Create a resume action and set notification as non-ongoing
            val resumeIntent = Intent(itsContext, PlayerService::class.java)
            resumeIntent.action = ACTION_RESUME
            val pendingIntentResume = PendingIntent.getService(
                itsContext,
                0,
                resumeIntent,
                getIntentFlagUpdateWithInMutable()
            )

            notificationBuilder.addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_check,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_resume),
                pendingIntentResume
            )
            notificationBuilder.setUsesChronometer(false)
            notificationBuilder.setDeleteIntent(pendingIntentStop)
            notificationBuilder.setOngoing(false)
        }

        // Add skip to next action
        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_skip_next_24dp,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_next),
            pendingIntentNext
        )

        // Configure the MediaStyle for the notification
        notificationBuilder.setStyle(
            MediaStyle()
                .setMediaSession(mediaSessionCompat.sessionToken)
                .setShowActionsInCompactView(1, 2, 3) // Previous, play/pause, next
                .setCancelButtonIntent(pendingIntentStop)
                .setShowCancelButton(true)
        )

        // Build the notification
        val notification = notificationBuilder.build()

        // Start the service as a foreground service with the notification
        doInTryCatch {
            service.startForeground(NotificationConst.NOTIFY_ID, notification)
        }

        notificationIsActive = true

        if (currentPlayerState === Paused || currentPlayerState === Idle) {
            // Make the notification dismissible
            service.stopForeground(false)
        }
    }

    fun sendMessage6(
        theTitle: String,
        theMessage: String,
        theTicker: String,
        radioLogo: String,
    ) {
        val itsContext = service as Context
        var theMessagee: String = theMessage ?: ""

        // Other parts of the function remain the same

        val notificationIntent = itsContext.getMainActivityStartIntent()
        prepareBitmapp(radioLogo)

        notificationIntent.flags = getIntentFlagUpdateWithInMutable()

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

        val playbackStateBuilder = PlaybackStateCompat.Builder()

        // Set the initial state (let's assume initially it's stopped)
        playbackStateBuilder.setState(
            PlaybackStateCompat.STATE_STOPPED,
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            1.0f
        )

        // Update state based on the actual state of your player
        val currentPlayerState: PlayState = getPlayState()
        when (currentPlayerState) {
            Playing -> playbackStateBuilder.setState(
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )

            Paused -> playbackStateBuilder.setState(
                PlaybackStateCompat.STATE_PAUSED,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )

            Idle -> playbackStateBuilder.setState(
                PlaybackStateCompat.STATE_STOPPED,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )
            // Add other states as necessary
            PrePlaying -> {
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_BUFFERING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1.0f
                )
            }
        }

        // Set capabilities for the supported actions
        playbackStateBuilder.setActions(
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_STOP
        )

        mediaSessionCompat.setPlaybackState(playbackStateBuilder.build())
        mediaSessionCompat.isActive = currentPlayerState != Idle

        // Rest of the code remains similar with changes in notificationBuilder and use of mediaSessionCompat
        val contentIntent = PendingIntent.getActivity(
            itsContext,
            0,
            notificationIntent,
            PlayerServiceUtils.getIntentFlagUpdateWithInMutable()
        )

        val notificationBuilder: Builder = Builder(
            itsContext,
            NotificationConst.notificationChannelId
        ).setContentIntent(contentIntent)
            .setContentTitle(theTitle)
            .setContentText(theMessage)
            .setWhen(System.currentTimeMillis())
            .setTicker(theTicker)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(com.oyetech.audioPlayerHelper.R.drawable.ic_play)
            .setLargeIcon(lastRadioBitmap)

        // Other notification setup remains the same
        val stopIntent =
            Intent(itsContext, PlayerService::class.java)
        stopIntent.action = ServiceConst.ACTION_STOP
        val pendingIntentStop =
            PendingIntent.getService(itsContext, 0, stopIntent, getIntentFlagUpdateWithInMutable())

        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_stop,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_stop),
            pendingIntentStop
        )
            .addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_skip_previous_24dp,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_previous),
                pendingIntentPrevious
            )



        notificationBuilder.setStyle(
            MediaStyle()
                .setMediaSession(mediaSessionCompat.sessionToken)
                .setShowActionsInCompactView(0, 1) // Define your compact view actions here
                .setCancelButtonIntent(pendingIntentStop)
                .setShowCancelButton(true)
        )

        val notification = notificationBuilder.build()

        doInTryCatch {
            service.startForeground(NotificationConst.NOTIFY_ID, notification)
        }

        notificationIsActive = true
        if (currentPlayerState == Paused || currentPlayerState == Idle) {
            service.stopForeground(false) // necessary to make notification dismissible
        }
    }

    fun sendMessage4(
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
                .getString(com.oyetech.audioPlayerHelper.R.string.notify_metered_connection)
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
            .setSmallIcon(com.oyetech.audioPlayerHelper.R.drawable.ic_play)
            .setLargeIcon(lastRadioBitmap)
            .addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_stop,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_stop),
                pendingIntentStop
            )
            .addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_skip_previous_24dp,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_previous),
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
                com.oyetech.audioPlayerHelper.R.drawable.ic_pause,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_pause),
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
                com.oyetech.audioPlayerHelper.R.drawable.ic_play,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_resume),
                pendingIntentResume
            )
            notificationBuilder.setUsesChronometer(false)
                .setDeleteIntent(pendingIntentStop)
                .setOngoing(false)
        }
        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_skip_next_24dp,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_next),
            pendingIntentNext
        )

            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(1, 2, 3/* previous, play/pause, next */)
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

    fun sendMessage2(
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
        Timber.d("playinggg notification come == " + currentPlayerState.toString())

        if ((currentPlayerState === Paused || currentPlayerState === Idle)
            && pauseReason === PauseReason.METERED_CONNECTION
        ) {
            theMessagee = itsContext.getResources()
                .getString(com.oyetech.audioPlayerHelper.R.string.notify_metered_connection)
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
            .setSmallIcon(com.oyetech.audioPlayerHelper.R.drawable.ic_play)
            .setLargeIcon(lastRadioBitmap)
            .addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_skip_previous_24dp,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_stop),
                pendingIntentStop
            )
            .addAction(
                com.oyetech.audioPlayerHelper.R.drawable.ic_skip_previous_24dp,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_previous),
                pendingIntentPrevious
            )
        if (currentPlayerState === Playing || currentPlayerState === PrePlaying) {
            Timber.d("playingggggggggggg")

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
                com.oyetech.audioPlayerHelper.R.drawable.ic_skip_next_24dp,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_pause),
                pendingIntentPause
            )
            notificationBuilder.setUsesChronometer(true)
                .setOngoing(true)
        } else if (currentPlayerState === Paused || currentPlayerState === Idle) {
            Timber.d("resumeeeeeeeeee")
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
                com.oyetech.audioPlayerHelper.R.drawable.ic_stop,
                itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_resume),
                pendingIntentResume
            )
            notificationBuilder.setUsesChronometer(false)
                .setDeleteIntent(pendingIntentStop)
                .setOngoing(false)
        }
        notificationBuilder.addAction(
            com.oyetech.audioPlayerHelper.R.drawable.ic_skip_next_24dp,
            itsContext.getString(com.oyetech.audioPlayerHelper.R.string.action_skip_to_next),
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
        return contentOperationUseCase.getPlayerState()
    }

}
