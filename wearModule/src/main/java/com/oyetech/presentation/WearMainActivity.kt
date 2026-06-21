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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
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
        setContent {
            val backStack = rememberNavBackStack(AppRoute.StopwatchDurationScreen)
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

            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    wearAppNavigation()
                }
            )
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
        if (stopwatchOperationUseCase.isFinishedPendingDisplay) {
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
        const val PREFS_NAME = "wear_timer_prefs"
        const val KEY_TIMER_FINISHED = "timer_finished"
        const val EXTRA_FROM_ALARM = "extra_from_alarm"
    }
}
