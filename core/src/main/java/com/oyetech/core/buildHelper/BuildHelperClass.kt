package com.oyetech.core.buildHelper

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
Created by Erdi Özbek
-16.07.2022-
-16:15-
 **/

fun Context.getVersionName(): String {
    var version = ""
    try {
        version = getPackageInfo().versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return version
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
