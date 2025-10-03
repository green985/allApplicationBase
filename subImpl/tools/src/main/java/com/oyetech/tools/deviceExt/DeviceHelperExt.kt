package com.oyetech.tools.deviceExt

import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.util.Log
import java.util.Locale

object DeviceHelperExt {
    fun getDeviceName(): String {
        var deviceName = ""
        try {
            deviceName = (if (MODEL.startsWith(MANUFACTURER, ignoreCase = true)) {
                MODEL
            } else {
                "$MANUFACTURER $MODEL"
            }).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        } catch (e: Exception) {
            Log.e("DeviceHelperExt", "getDeviceName: ", e)
        }
        return deviceName
    }

}
