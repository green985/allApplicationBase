package com.oyetech.tools.contextHelper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import com.oyetech.models.utils.const.ActivityNameConst
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-16.07.2022-
-17:30-
 **/

fun Context.openStoreUrl() {
    val appPackageName =
        packageName // getPackageName() from Context or Activity object

    try {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName")
            )
        )
    } catch (anfe: ActivityNotFoundException) {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
        )
    }
}

fun getIntentFlagUpdateWithInMutable(): Int {
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
        return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        return PendingIntent.FLAG_UPDATE_CURRENT
    }
}

fun Context.finishApp() {
    com.oyetech.tools.ext.doInTryCatch {
        (this as Activity).finishAffinity()
        System.exit(0)
    }
}

// Vibrate for 150 milliseconds
@SuppressLint("MissingPermission")
fun Context.shakePhone() {
    if (Build.VERSION.SDK_INT >= 26) {
        (this.getSystemService(VIBRATOR_SERVICE) as Vibrator)
            .vibrate(
                VibrationEffect.createOneShot(
                    HelperConstant.VIBRATE_TIME_MILIS,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
    } else {
        (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(HelperConstant.VIBRATE_TIME_MILIS)
    }
}

fun Context.removeAllNotification() {
    try {
        NotificationManagerCompat.from(this).cancelAll()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getMainActivityStartIntent(): Intent {
    val intent = Intent()
    intent.setClassName(this.packageName, ActivityNameConst.mainActivityName)

    return intent
}

fun Context.getAppName(): String {
    var context = this
    val pm: PackageManager = context.getPackageManager()
    val ai: ApplicationInfo?
    ai = try {
        pm.getApplicationInfo(context.getPackageName(), 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    val applicationName = (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun Context.getVersionName(): String {
    val context = this
    try {
        val versionName: String = context.getPackageManager()
            .getPackageInfo(context.getPackageName(), 0).versionName
        return versionName
    } catch (e: NameNotFoundException) {
        e.printStackTrace()
        return ""
    }
}

fun Context.getApplicationLogo(): Drawable? {
    try {
        val d = packageManager.getApplicationIcon(applicationInfo)
        return d
    } catch (e: NameNotFoundException) {
        return null
    }
}

fun Context.getVersionCode(): String {
    var version = ""
    try {
        version = getPackageInfo().versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return version
}

fun getPlatform(): String {
    return "g"
}

fun getOSVersion(): String {
    return Build.VERSION.SDK_INT.toString()
}

fun Context.getPackageInfo(): PackageInfo {
    val pInfo: PackageInfo =
        this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
    return pInfo
}
