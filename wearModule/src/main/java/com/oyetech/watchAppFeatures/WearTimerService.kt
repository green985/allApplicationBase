package com.oyetech.watchAppFeatures

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlarmManager
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
import com.oyetech.domain.repository.stopwatch.StopwatchSession
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.domain.useCases.StopwatchTickResult
import com.oyetech.presentation.TimerFinishedActivity
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
            stopSelf()
            return
        }
        val mins = remainingSeconds / SECONDS_IN_MINUTE
        val secs = remainingSeconds % SECONDS_IN_MINUTE
        val formatted = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        Timber.d("WearTimerService: tick remaining=$remainingSeconds fmt=$formatted fin=$isFinished")

        if (isFinished) {
            Timber.d("WearTimerService: timer finished — launching activity via AlarmManager")
            logBalDeviceState()
            stopwatchOperationUseCase.markFinishedPendingDisplay()
            persistFinishedFlag()
            vibrate()
            // PRIMARY launch path: send the activity PendingIntent ourselves with the SENDER-side
            // BAL opt-in (setPendingIntentBackgroundActivityStartMode). This sets
            // balAllowedByPiSender on the send, which the creator-side opt-in alone cannot do.
//            launchViaSenderBal()
            // SECONDARY launch path: AlarmManager.setAlarmClock() is BAL-exempt on all Android
            // versions (sender = AlarmManagerService, an allowlisted component). With the
            // USE_EXACT_ALARM permission auto-granted, canScheduleExactAlarms() is true so the
            // alarm actually fires and opens the activity from the background — no user-granted
            // full-screen-intent / overlay permission required (neither exists on Wear OS).
//            scheduleAlarmClock()
            // Detach the foreground notification BEFORE posting the finished one so stopSelf()
            // does not remove it. The notification is shown for visibility / as a tap target.
            stopForeground(STOP_FOREGROUND_DETACH)
            Timber.e("POSTING_FINISHED_NOTIFICATION")
            Timber.e(
                "POSTING_FINISHED_NOTIFICATION canUseFsi=%s",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    notificationManager.canUseFullScreenIntent()
                } else {
                    true
                }
            )
            notificationManager.notify(FINISHED_NOTIFICATION_ID, buildFinishedNotification())
            // Delay teardown so AlarmManager can fire the launch before the process is destroyed.
            mainHandler.postDelayed({ stopSelf() }, STOP_DELAY_MS)
        } else {
            notificationManager.notify(NOTIFICATION_ID, buildTickNotification(formatted))
        }
    }

    /**
     * PendingIntent that opens the dedicated alarm screen [TimerFinishedActivity].
     *
     * Used both as the AlarmManager.setAlarmClock() target and for the finished notification's
     * full-screen intent / content tap.
     *
     * The alarm is fired DIRECTLY at this activity PendingIntent (no BroadcastReceiver in
     * between): an alarm-clock PendingIntent that starts an activity is exempt from Background
     * Activity Launch restrictions because the sender is AlarmManagerService (an allowlisted
     * component). Routing it through a receiver and calling startActivity() there loses that
     * exemption (the launch becomes a non-PendingIntent start from RECEIVER proc state).
     *
     * FLAG_MUTABLE lets the sender (AlarmManager / NotificationManager) attach its own BAL
     * options when firing. On Android 14+ the creator opts in via
     * setPendingIntentCreatorBackgroundActivityStartMode (see [creatorBalOptions]); the SENDER
     * mode must NOT be set on the creator side as it throws IllegalArgumentException.
     */
    @SuppressLint("MutableImplicitPendingIntent")
    private fun buildFinishedActivityPendingIntent(requestCode: Int): PendingIntent {
        Timber.e(
            "CREATE_FINISHED_PI requestCode=%s",
            requestCode
        )
        val intent = Intent(this, TimerFinishedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        Timber.e(
            "CREATE_FINISHED_PI target=%s flags=MUTABLE|UPDATE_CURRENT",
            TimerFinishedActivity::class.java.simpleName
        )
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
     * Bundle that grants the PendingIntent SENDER background-activity-start privilege.
     * Used when WE call [PendingIntent.send] directly (we become the sender), so the platform
     * records balAllowedByPiSender for the launch instead of BSP.NONE. Returns null below
     * Android 14 (API 34) where the opt-in does not exist and BAL is already permitted.
     */
    private fun senderBalOptions(): Bundle? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return null
        val mode = if (Build.VERSION.SDK_INT >= ANDROID_16_SDK) {
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
        } else {
            ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        }
        return ActivityOptions.makeBasic()
            .setPendingIntentBackgroundActivityStartMode(mode)
            .toBundle()
    }

    /**
     * Sends the launch PendingIntent ourselves with the sender-side BAL opt-in
     * (setPendingIntentBackgroundActivityStartMode). This is the only way to set
     * balAllowedByPiSender — neither AlarmManager nor the creator-side opt-in can.
     */
    private fun launchViaSenderBal() {
        val pendingIntent = buildLaunchPendingIntent()
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                pendingIntent.send(senderBalOptions())
            } else {
                pendingIntent.send()
            }
            Timber.d("WearTimerService: launchViaSenderBal — PendingIntent sent with sender BAL opt-in")
        }.onFailure { Timber.e(it, "WearTimerService: launchViaSenderBal failed") }
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

    /**
     * The "launch" PendingIntent — this is the OPERATION fired by AlarmManager when the alarm
     * triggers. It is the one whose creator-side BAL opt-in matters because the system actually
     * sends() it from AlarmManagerService (an allowlisted sender). Carries its own request code
     * so it never collides with the show intent in the PendingIntent cache.
     */
    private fun buildLaunchPendingIntent(): PendingIntent =
        buildFinishedActivityPendingIntent(REQUEST_CODE_ALARM_LAUNCH)

    /**
     * The "show" PendingIntent — passed as AlarmClockInfo.showIntent. The system UI / system
     * alarm surfaces use this to let the user open/inspect the alarm; it is NOT the object the
     * alarm fires. It MUST be a different PendingIntent object (different request code) from the
     * launch intent, otherwise the platform treats them as the same token and the alarm's launch
     * inherits the show intent's (non-firing) BAL context.
     */
    private fun buildShowPendingIntent(): PendingIntent =
        buildFinishedActivityPendingIntent(REQUEST_CODE_ALARM_SHOW)

    /**
     * Schedules an immediate alarm clock via AlarmManager.setAlarmClock().
     *
     * Alarm-clock alarms are the only AlarmManager category whose firing PendingIntent is BAL
     * exempt (sender = AlarmManagerService). The showIntent and the operation (launch) intent are
     * intentionally TWO DISTINCT PendingIntent objects with separate request codes so the launch
     * operation carries its own creator BAL opt-in and is not conflated with the show token.
     * The activity is launched ~1 second after this call.
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
        val launchPendingIntent = buildLaunchPendingIntent()
        val showPendingIntent = buildShowPendingIntent()
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            System.currentTimeMillis() + ALARM_DELAY_MS,
            showPendingIntent,
        )
        alarmManager.setAlarmClock(alarmClockInfo, launchPendingIntent)
        Timber.d("WearTimerService: alarm clock scheduled — distinct show/launch PIs — opens in ${ALARM_DELAY_MS}ms")
    }

    private fun buildFinishedNotification(): Notification {
        Timber.e("BUILD_FINISHED_NOTIFICATION")
        val contentPendingIntent = buildFinishedActivityPendingIntent(REQUEST_CODE_CONTENT)
        // Use REQUEST_CODE_ALARM_LAUNCH so the full-screen intent uses its own token, separate from
        // the content tap, both opening the dedicated alarm screen TimerFinishedActivity.
        val fullScreenPendingIntent = buildFinishedActivityPendingIntent(REQUEST_CODE_ALARM_LAUNCH)
        Timber.e(
            "BUILD_FINISHED_NOTIFICATION contentPi=%s fullScreenPi=%s",
            contentPendingIntent,
            fullScreenPendingIntent
        )
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
        val pendingIntent = buildTickContentPendingIntent()
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
            NotificationManager.IMPORTANCE_MAX,
        ).apply {
            setShowBadge(false)
            setSound(

                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),

                AudioAttributes.Builder()

                    .setUsage(AudioAttributes.USAGE_ALARM)

                    .build()

            )
            enableVibration(true)
        }
        val existing = notificationManager.getNotificationChannel(CHANNEL_ID)

        Timber.e(

            "CHANNEL importance=%s vibration=%s sound=%s",

            existing?.importance,

            existing?.shouldVibrate(),

            existing?.sound

        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Timber.e("WearTimerService: onTaskRemoved")

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
        private const val REQUEST_CODE_ALARM_SHOW = 1005
        private const val CHANNEL_ID = "wear_timer_channel_v552"
        private const val SECONDS_IN_MINUTE = 60
        private const val ANDROID_16_SDK = 36
        private const val MAX_DURATION_MS = 25 * 60 * 1000L
        private const val ALARM_DELAY_MS = 1000L
        private const val STOP_DELAY_MS = 2000L
        private val VIBRATION_PATTERN = longArrayOf(0, 300, 200, 300, 200, 500)
        private val VIBRATION_AMPLITUDES = intArrayOf(0, 255, 0, 255, 0, 255)
    }
}
