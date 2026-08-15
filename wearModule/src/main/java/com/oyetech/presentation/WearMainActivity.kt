/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.oyetech.presentation

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.tiles.TileService
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.RadioAppTheme
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.watchAppFeatures.StopwatchDurationTileService
import com.oyetech.watchAppFeatures.wearAppNavigation
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class WearMainActivity : ComponentActivity() {

    private val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(
        NavigationUseCase::class.java
    )
    private val stopwatchOperationUseCase: StopwatchOperationUseCase by KoinJavaComponent.inject(
        StopwatchOperationUseCase::class.java
    )

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("WearMainActivity: POST_NOTIFICATIONS granted=$granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        requestFullScreenIntentPermissionIfNeeded()
        requestExactAlarmPermissionIfNeeded()
        restoreFinishedFlagIfNeeded(intent)
        val launchSeconds = intent?.getIntExtra(EXTRA_START_SECONDS, 0) ?: 0
        if (launchSeconds > 0) {
            startTimerService(launchSeconds)
        }
        setContent {
            val startRoute = if (launchSeconds > 0) {
                AppRoute.StopwatchScreen()
            } else {
                AppRoute.StopwatchDurationScreen
            }
            val backStack = rememberNavBackStack(startRoute)
            val coroutineScope = rememberCoroutineScope()

            SideEffect {
                navigationUseCase.setNavigator(
                    navigateTo = { route ->
                        coroutineScope.launch { backStack.add(route as NavKey) }
                    },
                    goBack = {
                        coroutineScope.launch {
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    }
                )
            }

            LaunchedEffect(Unit) {
                stopwatchOperationUseCase.onFinishedCleared.collect {
                    Timber.d("WearMainActivity: onFinishedCleared — removing KEY_TIMER_FINISHED from prefs")
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit().remove(KEY_TIMER_FINISHED).apply()
                    requestTileUpdate()
                }
            }

            RadioAppTheme {
                AppScaffold(
                    timeText = { TimeText() },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColors.background),
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            entryProvider = entryProvider {
                                wearAppNavigation()
                            }
                        )
                    }
                }
            }
        }

    }

    override fun onResume() {

        super.onResume()

        if (Settings.canDrawOverlays(this)) {

            Timber.d("Overlay permission granted")

        }

    }


    /**
     * Called when activity is already alive and AlarmManager fires or user taps notification.
     * Activity is not recreated — only onNewIntent is called.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.d(
            "WearMainActivity: onNewIntent fromAlarm=${
                intent.getBooleanExtra(
                    EXTRA_FROM_ALARM,
                    false
                )
            }"
        )
        restoreFinishedFlagIfNeeded(intent)
        val launchSeconds = intent.getIntExtra(EXTRA_START_SECONDS, 0)
        if (launchSeconds > 0) {
            startTimerService(launchSeconds)
            navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
            return
        }
        // Guard: if tickState.isFinished is already true, StopwatchVm is already showing the
        // finished state on StopwatchScreen. Navigating again would push a duplicate entry,
        // requiring the user to press "Bitir" twice to get back to the duration list.
        if (stopwatchOperationUseCase.isFinishedPendingDisplay &&
            !stopwatchOperationUseCase.tickState.value.isFinished
        ) {
            navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
        }
    }

    /**
     * If process was killed after timer finished, AlarmManager re-launches the activity.
     * The in-memory flag is gone but SharedPreferences persists the finished state.
     */
    private fun restoreFinishedFlagIfNeeded(intent: Intent?) {
        val fromAlarm = intent?.getBooleanExtra(EXTRA_FROM_ALARM, false) == true
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val wasFinished = prefs.getBoolean(KEY_TIMER_FINISHED, false)
        Timber.d("WearMainActivity: restoreFinishedFlagIfNeeded fromAlarm=$fromAlarm wasFinished=$wasFinished")
        if (wasFinished) {
            // Clear immediately so stale flag does not re-trigger on next cold launch
            prefs.edit().remove(KEY_TIMER_FINISHED).apply()
        }
        if (fromAlarm || wasFinished) {
            stopwatchOperationUseCase.markFinishedPendingDisplay()
        }
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            Timber.d("WearMainActivity: POST_NOTIFICATIONS already granted=$granted")
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startTimerService(seconds: Int) {
        val timerIntent = Intent(ACTION_START_TIMER).apply {
            setPackage(packageName)
            putExtra(EXTRA_SECONDS, seconds)
        }
        ContextCompat.startForegroundService(this, timerIntent)
        requestTileUpdate()
    }

    private fun requestTileUpdate() {
        runCatching {
            TileService.getUpdater(this)
                .requestUpdate(StopwatchDurationTileService::class.java)
        }.onFailure { Timber.w(it, "WearMainActivity: requestTileUpdate failed") }
    }

    /**
     * SCHEDULE_EXACT_ALARM is required for AlarmManager.setAlarmClock() on API 31+.
     * Without it, the timer finish alarm cannot auto-open the activity.
     * Redirects to system settings so the user can grant it once.
     */
    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            val canSchedule = alarmManager.canScheduleExactAlarms()
            Timber.d("WearMainActivity: canScheduleExactAlarms=$canSchedule")
            if (!canSchedule) {
                Timber.w("WearMainActivity: SCHEDULE_EXACT_ALARM not granted — redirecting to settings")
                runCatching {
                    startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:$packageName"),
                        )
                    )
                }.onFailure { Timber.e(it, "WearMainActivity: cannot open exact alarm settings") }
            }
        }
    }


    /**
     * USE_FULL_SCREEN_INTENT: lets NotificationManager fire the fullScreenIntent with BAL
     * allowance (balAllowedByPiSender → BAL_ALLOW_ALLOWLISTED_COMPONENT).
     * On Android 14+, auto-granted only for CATEGORY_ALARM apps or via user approval in Settings.
     */
    private fun requestFullScreenIntentPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val nm = getSystemService(NotificationManager::class.java)
            val canUse = nm.canUseFullScreenIntent()
            Timber.d("WearMainActivity: canUseFullScreenIntent=$canUse")
            if (!canUse) {
                runCatching {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                            Uri.parse("package:$packageName"),
                        )
                    )
                }.onFailure {
                    Timber.e(
                        it,
                        "WearMainActivity: cannot open full screen intent settings"
                    )
                }
            }
        }
    }

    companion object {
        private const val ACTION_START_TIMER = "com.oyetech.wear.ACTION_START_TIMER"
        private const val EXTRA_SECONDS = "seconds"
        const val EXTRA_START_SECONDS = "extra_start_seconds"
        const val PREFS_NAME = "wear_timer_prefs"
        const val KEY_TIMER_FINISHED = "timer_finished"
        const val EXTRA_FROM_ALARM = "extra_from_alarm"
    }
}
