package com.oyetech.tools.debug

import android.app.Activity
import android.app.KeyguardManager
import android.content.pm.ApplicationInfo
import android.os.Build
import android.view.WindowManager
import com.oyetech.tools.debug.DebugUnlockHelper.unlockForDebug

/**
 * Created by Erdi Özbek
 *
 * A utility helper that dismisses the phone's lock screen / keyguard protection
 * automatically when launching the app from Android Studio.
 *
 * This helper is a **debug-only** operation – it performs a no-op in production
 * builds (i.e. non-debuggable APKs).
 *
 * Usage – call [unlockForDebug] inside your Activity's onCreate():
 *
 * ```kotlin
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     DebugUnlockHelper.unlockForDebug(this)
 * }
 * ```
 */
object DebugUnlockHelper {

    /**
     * Turns the screen on and dismisses the keyguard so the app is immediately
     * visible after being launched from Android Studio.
     *
     * Safe to ship in release builds – the call is silently ignored when
     * [ApplicationInfo.FLAG_DEBUGGABLE] is not set.
     *
     * @param activity The activity that should be shown on top of the lock screen.
     */
    fun unlockForDebug(activity: Activity) {
        if (!isDebuggable(activity)) return

        turnScreenOnAndShowAboveLockScreen(activity)
        dismissKeyguard(activity)
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Returns `true` when the running process was signed / built as a
     * debuggable application (i.e. debug builds from Android Studio).
     */
    private fun isDebuggable(activity: Activity): Boolean =
        activity.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    /**
     * Sets the window flags that allow the activity to be displayed while the
     * screen is off or the device is locked, and also wakes up the screen.
     *
     * - API 27+ : uses the non-deprecated Activity methods.
     * - API 26  : falls back to the legacy [WindowManager.LayoutParams] flags.
     */
    private fun turnScreenOnAndShowAboveLockScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // API 27+ preferred approach
            activity.setShowWhenLocked(true)
            activity.setTurnScreenOn(true)
        } else {
            // API 26 fallback – flags are deprecated above API 26 but still work
            @Suppress("DEPRECATION")
            activity.window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    /**
     * Requests the system to dismiss the keyguard (PIN / pattern / fingerprint
     * lock screen) so the activity is shown straight away.
     *
     * Available from API 26 ([KeyguardManager.requestDismissKeyguard]).
     */
    private fun dismissKeyguard(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager =
                activity.getSystemService(KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(activity, null)
        }
    }
}

