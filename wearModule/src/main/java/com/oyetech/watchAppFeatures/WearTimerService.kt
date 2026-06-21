package com.oyetech.watchAppFeatures

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.domain.useCases.StopwatchTickResult
import com.oyetech.presentation.WearMainActivity
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

        startForeground(NOTIFICATION_ID, buildTickNotification("Starting…"))

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
                updateNotification(tick.remainingSeconds, tick.isFinished, tick.isCancelled)
            }
        }
    }

    private fun updateNotification(remainingSeconds: Int, isFinished: Boolean, isCancelled: Boolean) {
        if (isCancelled) {
            Timber.d("WearTimerService: timer cancelled — stopping")
            stopSelf()
            return
        }
        val mins = remainingSeconds / SECONDS_IN_MINUTE
        val secs = remainingSeconds % SECONDS_IN_MINUTE
        val formatted = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        Timber.d("WearTimerService: tick remaining=$remainingSeconds fmt=$formatted fin=$isFinished")

        if (isFinished) {
            Timber.d("WearTimerService: timer finished — launching activity via AlarmManager")
            stopwatchOperationUseCase.markFinishedPendingDisplay()
            persistFinishedFlag()
            vibrate()
            // AlarmManager.setAlarmClock() is BAL-exempt on all Android versions including
            // targetSdk 36. The system fires the PendingIntent as an alarm clock entry,
            // bypassing background activity launch restrictions entirely.
            scheduleAlarmClock()
            notificationManager.notify(NOTIFICATION_ID, buildFinishedNotification())
            stopSelf()
        } else {
            notificationManager.notify(NOTIFICATION_ID, buildTickNotification(formatted))
        }
    }

    private fun buildOpenAppPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(this, WearMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(WearMainActivity.EXTRA_FROM_ALARM, true)
        }
        return PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /**
     * Schedules an immediate alarm clock via AlarmManager.setAlarmClock().
     * Alarm clock intents are exempt from Background Activity Launch (BAL) restrictions
     * on all Android versions, including apps targeting SDK 35+.
     * The activity will be launched ~1 second after this call.
     */
    private fun scheduleAlarmClock() {
        val alarmManager = getSystemService(AlarmManager::class.java) ?: run {
            Timber.e("WearTimerService: AlarmManager not available")
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Timber.w("WearTimerService: SCHEDULE_EXACT_ALARM not granted — notification fallback only")
            return
        }
        val pendingIntent = buildOpenAppPendingIntent(REQUEST_CODE_ALARM)
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            System.currentTimeMillis() + ALARM_DELAY_MS,
            pendingIntent,
        )
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        Timber.d("WearTimerService: alarm clock scheduled — activity will open in ${ALARM_DELAY_MS}ms")
    }

    private fun buildFinishedNotification(): Notification {
        val contentPendingIntent = buildOpenAppPendingIntent(REQUEST_CODE_CONTENT)
        val fullScreenPendingIntent = buildOpenAppPendingIntent(REQUEST_CODE_FULL_SCREEN)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer finished")
            .setContentText("Time's up! Tap to view results.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(contentPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()
    }

    private fun buildTickNotification(text: String): Notification {
        val pendingIntent = buildOpenAppPendingIntent(REQUEST_CODE_CONTENT)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun persistFinishedFlag() {
        Timber.d("WearTimerService: persistFinishedFlag")
        getSharedPreferences(WearMainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(WearMainActivity.KEY_TIMER_FINISHED, true)
            .apply()
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
        val effect = VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_AMPLITUDES, -1)
        vibrator.vibrate(effect)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }
        notificationManager.createNotificationChannel(channel)
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
        private const val REQUEST_CODE_ALARM = 1002
        private const val REQUEST_CODE_CONTENT = 1003
        private const val REQUEST_CODE_FULL_SCREEN = 1004
        private const val CHANNEL_ID = "wear_timer_channel_v2"
        private const val SECONDS_IN_MINUTE = 60
        private const val MAX_DURATION_MS = 25 * 60 * 1000L
        private const val ALARM_DELAY_MS = 1000L
        private val VIBRATION_PATTERN = longArrayOf(0, 300, 200, 300, 200, 500)
        private val VIBRATION_AMPLITUDES = intArrayOf(0, 255, 0, 255, 0, 255)
    }
}
