package com.oyetech.watchAppFeatures

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.wear.tiles.TileService
import com.oyetech.domain.repository.stopwatch.StopwatchSession
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

    private val mainHandler = Handler(Looper.getMainLooper())

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
        val seconds = intent?.getIntExtra("seconds", 0) ?: 0
        Timber.d("WearTimerService: onStartCommand seconds=$seconds")

        startForeground(NOTIFICATION_ID, buildTickNotification("Starting…"))

        when {
            seconds > 0 -> collectFlow {
                stopwatchOperationUseCase.startCountdown(StopwatchSession(durationSeconds = seconds))
            }

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

    private fun updateNotification(
        remainingSeconds: Int,
        isFinished: Boolean,
        isCancelled: Boolean,
    ) {
        if (isCancelled) {
            Timber.d("WearTimerService: timer cancelled — stopping")
            requestTileUpdate()
            stopSelf()
            return
        }
        val mins = remainingSeconds / SECONDS_IN_MINUTE
        val secs = remainingSeconds % SECONDS_IN_MINUTE
        val formatted = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        Timber.d("WearTimerService: tick remaining=$remainingSeconds fmt=$formatted fin=$isFinished")

        if (isFinished) {
            Timber.d("WearTimerService: timer finished")
            logBalDeviceState()
            stopwatchOperationUseCase.markFinishedPendingDisplay()
            persistFinishedFlag()
            vibrate()
            stopForeground(STOP_FOREGROUND_REMOVE)
            notificationManager.notify(FINISHED_NOTIFICATION_ID, buildFinishedNotification())
            requestTileUpdate()
            mainHandler.postDelayed({ stopSelf() }, STOP_DELAY_MS)
        } else {
            notificationManager.notify(NOTIFICATION_ID, buildTickNotification(formatted))
        }
    }

    private fun requestTileUpdate() {
        runCatching {
            TileService.getUpdater(this)
                .requestUpdate(StopwatchDurationTileService::class.java)
        }.onFailure { Timber.w(it, "WearTimerService: requestTileUpdate failed") }
    }

    /**
     * PendingIntent that opens [WearMainActivity] with EXTRA_FROM_ALARM=true when the timer finishes.
     *
     * Used for the finished notification's full-screen intent and content tap.
     *
     * FLAG_MUTABLE lets the sender (NotificationManager) attach its own BAL options when firing.
     * On Android 14+ the creator opts in via setPendingIntentCreatorBackgroundActivityStartMode.
     */
    @SuppressLint("MutableImplicitPendingIntent")
    private fun buildFinishedActivityPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(this, WearMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(WearMainActivity.EXTRA_FROM_ALARM, true)
        }
        return PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            creatorBalOptions(),
        )
    }

    /**
     * Bundle that opts the PendingIntent creator in to background activity starts.
     * Required on Android 14+ (UPSIDE_DOWN_CAKE / API 34) — returns null on older
     * versions where the opt-in does not exist and BAL is already permitted.
     */
    private fun creatorBalOptions(): Bundle? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return null
        val mode = if (Build.VERSION.SDK_INT >= 36) {
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
        } else {
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        }
        return ActivityOptions.makeBasic()
            .setPendingIntentCreatorBackgroundActivityStartMode(mode)
            .toBundle()
    }

    /**
     * Diagnostics for the BAL investigation: correlates the ActivityTaskManager verdict with the
     * device state at the exact moment the launch is attempted. A full-screen intent only gets
     * sender BAL from NotificationManagerService when it is actually elevated — which on Wear
     * typically requires the screen to be non-interactive / keyguard showing.
     */
    private fun logBalDeviceState() {
        val powerManager = getSystemService(PowerManager::class.java)
        val keyguardManager = getSystemService(android.app.KeyguardManager::class.java)
        val canUseFsi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            notificationManager.canUseFullScreenIntent()
        } else {
            true
        }
        Timber.d(
            "WearTimerService: BAL state interactive=%s deviceLocked=%s keyguardLocked=%s canUseFsi=%s",
            powerManager?.isInteractive,
            keyguardManager?.isDeviceLocked,
            keyguardManager?.isKeyguardLocked,
            canUseFsi,
        )
    }

    /**
     * PendingIntent for the running-timer (tick) notification tap.
     * Does NOT carry EXTRA_FROM_ALARM — opening the app while the timer is running
     * must not mark the session as finished.
     */
    private fun buildTickContentPendingIntent(): PendingIntent {
        val intent = Intent(this, WearMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        return PendingIntent.getActivity(
            this,
            REQUEST_CODE_CONTENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildFinishedNotification(): Notification {
        val contentPendingIntent = buildFinishedActivityPendingIntent(REQUEST_CODE_CONTENT)
        val fullScreenPendingIntent = buildFinishedActivityPendingIntent(REQUEST_CODE_ALARM_LAUNCH)
        return NotificationCompat.Builder(this, FINISHED_CHANNEL_ID)
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
        val pendingIntent = buildTickContentPendingIntent()
        return NotificationCompat.Builder(this, TICK_CHANNEL_ID)
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
        // Do NOT pass amplitude array — most Wear OS hardware reports hasAmplitudeControl()=false
        // and silently ignores or aborts the effect when amplitudes are provided.
        // The no-amplitude overload (repeat=-1 means play once) uses full power on all devices.
        val effect = VibrationEffect.createWaveform(VIBRATION_PATTERN, -1)
        vibrator.vibrate(effect)
    }

    private fun createNotificationChannel() {
        // Tick channel: no sound, no vibration — the foreground countdown notification
        // should not buzz on every update (setOnlyAlertOnce handles the first alert).
        val tickChannel = NotificationChannel(
            TICK_CHANNEL_ID,
            "Timer Countdown",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }
        notificationManager.createNotificationChannel(tickChannel)

        // Finished channel: sound only, vibration disabled at channel level so the
        // explicit vibrator.vibrate() call in vibrate() runs without the channel
        // cancelling it with a short default buzz.
        val finishedChannel = NotificationChannel(
            FINISHED_CHANNEL_ID,
            "Timer Finished",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            setShowBadge(false)
            enableVibration(false)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
        }
        notificationManager.createNotificationChannel(finishedChannel)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("WearTimerService: onDestroy")
        mainHandler.removeCallbacksAndMessages(null)
        wakeLock?.let {
            if (it.isHeld) it.release()
            Timber.d("WearTimerService: WakeLock released")
        }
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE_ALARM_LAUNCH = 1002
        private const val REQUEST_CODE_CONTENT = 1003
        private const val FINISHED_NOTIFICATION_ID = 1004
        private const val TICK_CHANNEL_ID = "wear_timer_tick_v1"
        private const val FINISHED_CHANNEL_ID = "wear_timer_finished_v1"
        private const val SECONDS_IN_MINUTE = 60
        private const val MAX_DURATION_MS = 25 * 60 * 1000L
        private const val STOP_DELAY_MS = 2000L
        private val VIBRATION_PATTERN =
            longArrayOf(0, 900, 200, 900, 200, 900, 200, 900, 200, 900, 200, 900, 200, 900, 200, 900, 200, 900, 200, 900)
    }
}
