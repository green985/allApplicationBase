package com.oyetech.watchAppFeatures

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class WearTimerService : Service() {

    private val stopwatchOperationUseCase: StopwatchOperationUseCase by KoinJavaComponent.inject(
        StopwatchOperationUseCase::class.java
    )
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("WearTimerService: onCreate")
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val minutes = intent?.getIntExtra("minutes", 0) ?: 0
        Timber.d("WearTimerService: onStartCommand minutes=$minutes intent=$intent")
        if (minutes <= 0) {
            Timber.w("WearTimerService: minutes <= 0, stopping self")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification("Starting…"))
        Timber.d("WearTimerService: foreground started, launching countdown")
        startCountdown(minutes)
        return START_STICKY
    }

    private fun startCountdown(minutes: Int) {
        Timber.d("WearTimerService: startCountdown minutes=$minutes")
        serviceScope.launch {
            stopwatchOperationUseCase.startCountdown(minutes).collect { tick ->
                val mins = tick.remainingSeconds / SECONDS_IN_MINUTE
                val secs = tick.remainingSeconds % SECONDS_IN_MINUTE
                val formatted =
                    "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
                Timber.d(
                    "WearTimerService: tick remaining=${tick.remainingSeconds} fmt=$formatted fin=${tick.isFinished}"
                )

                if (tick.isFinished) {
                    Timber.d("WearTimerService: timer finished — notifying and stopping")
                    notificationManager.notify(NOTIFICATION_ID, buildNotification("Time's up!"))
                    vibrate()
                    stopSelf()
                } else {
                    notificationManager.notify(NOTIFICATION_ID, buildNotification(formatted))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {
        val vibrator = getSystemService(Vibrator::class.java) ?: return
        val effect = VibrationEffect.createWaveform(
            VIBRATION_PATTERN,
            VIBRATION_AMPLITUDES,
            -1,
        )
        vibrator.vibrate(effect)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("WearTimerService: onDestroy")
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "wear_timer_channel"
        private const val SECONDS_IN_MINUTE = 60
        private val VIBRATION_PATTERN = longArrayOf(0, 300, 200, 300, 200, 500)
        private val VIBRATION_AMPLITUDES = intArrayOf(0, 255, 0, 255, 0, 255)
    }
}
