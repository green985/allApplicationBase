package com.oyetech.presentation

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oyetech.composebase.projectQuestionsFeature.theme.AppSpacing
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.presentation.theme.AllApplicationBaseTheme
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
 * Dedicated full-screen alarm activity shown when the countdown finishes.
 *
 * Unlike the launcher [WearMainActivity], this is a single-purpose alarm UI that the system
 * treats as a legitimate alarm screen: it turns the screen on and shows over the lock screen,
 * which is the expected behavior for an AlarmManager.setAlarmClock() launch on Wear OS.
 *
 * It is launched by TimerAlarmReceiver, which receives the alarm-clock broadcast and is
 * therefore granted a temporary Background Activity Launch allowlist token — this is what
 * makes the launch reliable on Android 14/15/16 (SDK 36).
 */
class TimerFinishedActivity : ComponentActivity() {

    private val stopwatchOperationUseCase: StopwatchOperationUseCase by KoinJavaComponent.inject(
        StopwatchOperationUseCase::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("TimerFinishedActivity: onCreate")
        super.onCreate(savedInstanceState)
        showWhenLockedAndTurnScreenOn()
        enableEdgeToEdge()
        stopwatchOperationUseCase.markFinishedPendingDisplay()
        setContent {
            AllApplicationBaseTheme {
                TimerFinishedScreen(
                    onViewResults = ::openResults,
                    onDismiss = ::dismiss,
                )
            }
        }
    }

    /**
     * Turns the screen on and shows the activity over the lock screen — the alarm behavior.
     * setShowWhenLocked/setTurnScreenOn are the runtime counterparts of the manifest
     * android:showWhenLocked / android:turnScreenOn attributes; both are declared so the
     * window is configured as early as possible. On API 26 we fall back to window flags.
     */
    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            )
        }
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)
    }

    private fun openResults() {
        Timber.d("TimerFinishedActivity: openResults")
        startActivity(
            Intent(this, WearMainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(WearMainActivity.EXTRA_FROM_ALARM, true)
            }
        )
        finish()
    }

    private fun dismiss() {
        Timber.d("TimerFinishedActivity: dismiss")
        stopwatchOperationUseCase.clearFinishedPendingDisplay()
        finish()
    }
}

@Composable
private fun TimerFinishedScreen(
    onViewResults: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.xxl, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Time's up!",
            style = AppTextStyles.titleLarge,
        )
        Spacer(modifier = Modifier.height(AppSpacing.md))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onViewResults,
            contentPadding = PaddingValues(
                horizontal = AppSpacing.sm,
                vertical = AppSpacing.xs,
            ),
        ) {
            Text(
                text = "View results",
                style = AppTextStyles.bodySecondary,
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss,
            contentPadding = PaddingValues(
                horizontal = AppSpacing.sm,
                vertical = AppSpacing.xs,
            ),
        ) {
            Text(
                text = "Dismiss",
                style = AppTextStyles.bodySecondary,
            )
        }
    }
}


