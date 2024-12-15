package com.oyetech.core.deviceExt

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.util.Base64
import android.util.Log
import com.oyetech.core.ext.doInTryCatch
import java.security.MessageDigest
import java.util.Locale

object DeviceHelperExt {
    fun getDeviceName(): String {
        var deviceName = ""
        doInTryCatch {
            deviceName = if (MODEL.startsWith(MANUFACTURER, ignoreCase = true)) {
                MODEL
            } else {
                "$MANUFACTURER $MODEL"
            }.capitalize(Locale.ROOT)
        }
        return deviceName
    }



    fun printHashKey(context: Context): String {
        try {
            val info: PackageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("AppLog", "key:$hashKey=")
                return hashKey
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }
        return "error"
    }
}
