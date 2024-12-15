package com.oyetech.extension

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner

/**
Created by Erdi Özbek
-4.04.2022-
-00:10-
 **/

fun Context.startActivityWithClassName(className: String) {
    val intent = Intent()
    intent.setClassName(this.packageName, className)
    startActivity(intent)
}

fun Context.startActivityWithClearStack(className: String) {
    val intent = Intent()
    intent.setClassName(this.packageName, className)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

fun Context.isApplicationBroughtToBackground(): Boolean {
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val tasks = am.getRunningTasks(1)
    if (!tasks.isEmpty()) {
        val topActivity = tasks[0].topActivity
        if (topActivity!!.packageName != this.getPackageName()) {
            return true
        }
    }
    return false
}

fun appIsOrWasInBackground(): Boolean {
    val currentState: Lifecycle.State = ProcessLifecycleOwner.get().lifecycle.currentState
    return currentState === Lifecycle.State.CREATED || currentState === Lifecycle.State.STARTED
// CREATED is when the app is currently in the background.
// STARTED is when the app is coming from the background,
// for example, from a `startActivity` call from a background Service or BroadcastReceiver.
// Either of these mean it _is_ or _was_ in the background (and should probably stay there).
}
