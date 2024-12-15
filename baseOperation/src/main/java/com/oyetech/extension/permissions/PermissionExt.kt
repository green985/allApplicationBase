package com.oyetech.extension.permissions

/**
Created by Erdi Özbek
-24.03.2022-
-00:10-
 **/

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oyetech.base.BaseFragment
import com.oyetech.models.utils.const.PermissionConstant

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int,
) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

fun BaseFragment<*, *>.requestPermission(permissions: Array<String>) {
    activity?.let {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(it, permissions, 101)
        }
    }
}

fun Context.checkIsPermissionAlreadyGranted(permissions: Array<String>): Boolean {
    permissions.forEach { permission ->
        val isGranted = this?.let {
            ContextCompat.checkSelfPermission(it, permission)
        } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            return false
        }
    }
    return true
}

fun Context.checkPermissionForSendImage(): Boolean {
    var permissions = PermissionConstant.getSendImagePermissionListNewApi()
    permissions.forEach { permission ->
        val isGranted = this?.let {
            ContextCompat.checkSelfPermission(it, permission)
        } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            return false
        }
    }
    return true
}

fun Context.checkPermissionForNotification(): Boolean {
    var permissions = PermissionConstant.getPostNotificationPermission()
    permissions.forEach { permission ->
        val isGranted = this?.let {
            ContextCompat.checkSelfPermission(it, permission)
        } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            return false
        }
    }
    return true
}

fun Context.checkPermissionWriteData(): Boolean {
    var permissions = PermissionConstant.writeExternalPermission
    val isGranted = this?.let {
        ContextCompat.checkSelfPermission(it, permissions)
    } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
    if (!isGranted) {
        return false
    }

    return true
}

fun Context.checkPermissionCamera(): Boolean {
    var permissions = PermissionConstant.cameraPermission
    val isGranted = this?.let {
        ContextCompat.checkSelfPermission(it, permissions)
    } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
    if (!isGranted) {
        return false
    }

    return true
}

fun Context.checkPermissionForLocation(): Boolean {
    var permissions = PermissionConstant.locationPermissionList
    permissions.forEach { permission ->
        val isGranted = this?.let {
            ContextCompat.checkSelfPermission(it, permission)
        } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            return false
        }
    }
    return true
}

fun Context.checkPermissionForRecordVoice(): Boolean {
    var permissions = PermissionConstant.getSendAudioPermissionListNewApi()
    permissions.forEach { permission ->
        val isGranted = this?.let {
            ContextCompat.checkSelfPermission(it, permission)
        } ?: PackageManager.PERMISSION_DENIED == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            return false
        }
    }
    return true
}

fun Activity.isLocationPermissionNotAskedClick(): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        PermissionConstant.locationPermission
    )
}

fun Activity.isWritePermissionNotAskedClick(): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        PermissionConstant.writeExternalPermission
    )
}

fun Activity.isNotificationPermissionNotAskedClick(): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        PermissionConstant.postNotification
    )
}
