package com.oyetech.watchAppFeatures

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.domain.useCases.StopwatchTickResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

@Suppress("TooManyFunctions")
class WearTimerService : Service() {

    private val stopwatchOperationUseCase: StopwatchOperationUseCase by KoinJavaComponent.inject(
        StopwatchOperationUseCase::class.java
    )
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var countdownJob: Job? = null

    private lateinit var notificationManager: NotificationManager
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("WearTimerService: onCreate")
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val minutes = intent?.getIntExtra("minutes", 0) ?: 0
        Timber.d("WearTimerService: onStartCommand minutes=$minutes")

        startForeground(NOTIFICATION_ID, buildNotification("Starting…"))

        when {
            minutes > 0 -> collectFlow { stopwatchOperationUseCase.startCountdown(minutes) }
            stopwatchOperationUseCase.hasActiveSession() -> collectFlow { stopwatchOperationUseCase.resumeCountdown() }
            else -> {
                Timber.w("WearTimerService: no active session — stopping")
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    private fun collectFlow(flowProvider: () -> Flow<StopwatchTickResult>) {
        countdownJob?.cancel()
        countdownJob = serviceScope.launch {
            flowProvider().collect { tick ->
                updateNotification(tick.remainingSeconds, tick.isFinished)
            }
        }
    }

    private fun updateNotification(remainingSeconds: Int, isFinished: Boolean) {
        val mins = remainingSeconds / SECONDS_IN_MINUTE
        val secs = remainingSeconds % SECONDS_IN_MINUTE
        val formatted = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        Timber.d("WearTimerService: tick remaining=$remainingSeconds fmt=$formatted fin=$isFinished")

        if (isFinished) {
            Timber.d("WearTimerService: timer finished")
            notificationManager.notify(NOTIFICATION_ID, buildNotification("Time's up!"))
            vibrate()
            stopSelf()
        } else {
            notificationManager.notify(NOTIFICATION_ID, buildNotification(formatted))
        }
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(PowerManager::class.java)
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "WearTimerService::countdown"
        ).also {
            it.acquire(MAX_DURATION_MS)
            Timber.d("WearTimerService: WakeLock acquired")
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
        wakeLock?.let {
            if (it.isHeld) it.release()
            Timber.d("WearTimerService: WakeLock released")
        }
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "wear_timer_channel"
        private const val SECONDS_IN_MINUTE = 60
        private const val MAX_DURATION_MS = 25 * 60 * 1000L // 25 min safety margin
        private val VIBRATION_PATTERN = longArrayOf(0, 300, 200, 300, 200, 500)
        private val VIBRATION_AMPLITUDES = intArrayOf(0, 255, 0, 255, 0, 255)
    }
}
