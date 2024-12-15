package com.oyetech.models.utils.const

import android.Manifest
import android.os.Build

/**
Created by Erdi Özbek
-6.04.2022-
-14:27-
 **/

object PermissionConstant {

    const val sendImageRequestCode = 1000
    const val sendVoiceRequestCode = 2000
    const val locationRequestCode = 3000
    const val notificationPermissionCode = 4000
    const val writePermissionCode = 5000

    const val cameraPermission = Manifest.permission.CAMERA
    const val writeExternalPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val readImagePermissionApi33 = Manifest.permission.READ_MEDIA_IMAGES
    const val recordVoicePermission = Manifest.permission.RECORD_AUDIO
    const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    const val postNotification = Manifest.permission.POST_NOTIFICATIONS

    private var sendImagePermissionList =
        arrayOf(PermissionConstant.cameraPermission, PermissionConstant.writeExternalPermission)

    fun getSendImagePermissionListNewApi(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                PermissionConstant.cameraPermission,
                PermissionConstant.readImagePermissionApi33
            )
        } else {
            sendImagePermissionList
        }
    }

    fun getPostNotificationPermission(): Array<String> {
        return arrayOf(postNotification)
    }

    fun getSendAudioPermissionListNewApi(): Array<String> {
        return sendAudioPermissionList
    }

    var locationPermissionList = arrayOf(locationPermission)

    private var sendAudioPermissionList =
        arrayOf(
            PermissionConstant.recordVoicePermission,
            // PermissionConstant.writeExternalPermission
        )
}
