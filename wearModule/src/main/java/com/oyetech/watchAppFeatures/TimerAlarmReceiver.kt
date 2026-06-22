package com.oyetech.watchAppFeatures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oyetech.presentation.TimerFinishedActivity
import timber.log.Timber

/**
 * Receives the AlarmManager.setAlarmClock() broadcast when the countdown finishes and starts
 * the dedicated alarm screen [TimerFinishedActivity].
 *
 * Why a BroadcastReceiver instead of an activity PendingIntent:
 * When AlarmManager delivers an alarm-clock broadcast, the receiver's app is placed on a
 * temporary power + Background Activity Launch allowlist for ~10 seconds. Starting the activity
 * from inside onReceive() is therefore BAL-allowed on Android 14/15/16 (SDK 36), whereas a
 * direct activity PendingIntent to the launcher activity was being rejected as an illegitimate
 * background launch (BAL_BLOCK / resultIfPiCreatorAllowsBal = BAL_BLOCK).
 */
class TimerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("TimerAlarmReceiver: onReceive — launching TimerFinishedActivity")
        val launchIntent = Intent(context, TimerFinishedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        runCatching { context.startActivity(launchIntent) }
            .onFailure { Timber.e(it, "TimerAlarmReceiver: startActivity failed") }
    }

    companion object {
        const val ACTION_TIMER_FINISHED = "com.oyetech.wear.ACTION_TIMER_FINISHED"
    }
}

