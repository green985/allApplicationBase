package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.VolumeProvider
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ServiceCompat
import androidx.media.VolumeProviderCompat
import timber.log.Timber

class DigitalNoteForegroundService : Service() {
    private val notificationFactory = DigitalNoteNotificationFactory()
    private lateinit var mediaSession: MediaSession
    private lateinit var volumeProvider: VolumeProviderCompat
    private val doublePressDetector = DoublePressDetector()
    private var mainActivityClass: Class<out Activity>? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notificationFactory.createChannel(this)
        setupMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startInForeground("Listening for quick notes")
        val activityClassName = intent?.getStringExtra("main_activity_class")
        if (!activityClassName.isNullOrBlank()) {
            try {
                val clazz = Class.forName(activityClassName)
                if (Activity::class.java.isAssignableFrom(clazz)) {
                    @Suppress("UNCHECKED_CAST")
                    mainActivityClass = clazz as Class<out Activity>
//                    updateNotification("Listening for quick notes")
                }
            } catch (_: Throwable) {
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        try {
            mediaSession.isActive = false
            mediaSession.release()
        } catch (_: Throwable) {
        }
        super.onDestroy()
    }

    private fun startInForeground(contentText: String) {
        val mainClass = mainActivityClass ?: Activity::class.java
        val notification =
            notificationFactory.buildForegroundNotification(this, contentText, mainClass)


        ServiceCompat.startForeground(
            this, DigitalNoteNotificationIds.NOTIFICATION_ID, notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            },
        )
        Timber.d("DigitalNoteForegroundService started in foreground")
    }

    private fun updateNotification(contentText: String) {
        val mainClass = mainActivityClass ?: Activity::class.java
        val notification =
            notificationFactory.buildForegroundNotification(this, contentText, mainClass)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(DigitalNoteNotificationIds.NOTIFICATION_ID, notification)
    }

    private fun setupMediaSession() {
        mediaSession = MediaSession(this, "DigitalNoteMediaSession")
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setPlaybackState(
            PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_PLAY_PAUSE
                )
                .setState(PlaybackState.STATE_PLAYING, 0L, 1f)
                .build()
        )
        volumeProvider =
            object : VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 100, 50) {
                override fun onAdjustVolume(direction: Int) {
                    when {
                        direction > 0 -> handleVolumeKey(VolumeKeyType.Up)
                        direction < 0 -> handleVolumeKey(VolumeKeyType.Down)
                    }
                }
            }

        mediaSession.setPlaybackToRemote(volumeProvider.volumeProvider as VolumeProvider)
        mediaSession.isActive = true
    }

    private fun handleVolumeKey(type: VolumeKeyType) {
        val isDouble = doublePressDetector.onKey(type)
        if (isDouble) {
            DigitalNoteActionDispatcher.dispatch(type)
            haptic()
        }
    }

    @SuppressLint("MissingPermission")
    private fun haptic() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(25L, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}