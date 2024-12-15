package com.oyetech.core.appUtil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.util.Log

object AppUtil {
    /**
     * Restarts the application. Should generally only be used for internal tools.
     */
    fun restart(context: Context) {
        val packageName = context.packageName
        val defaultIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        defaultIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(defaultIntent)
        Runtime.getRuntime().exit(0)
    }

    @SuppressLint("MissingPermission")
    fun hasAnyConnection(context: Context): Boolean {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }

    fun dumpIntent(i: Intent) {
        var LOG_TAG = "intentnnn"
        val bundle = i.extras


        if (bundle != null) {
            val keys = bundle.keySet()
            val it: Iterator<String> = keys.iterator()
            Log.e(LOG_TAG, "Dumping Intent start")
            while (it.hasNext()) {
                val key = it.next()
                Log.e(LOG_TAG, "[" + key + "=" + bundle[key] + "]")
            }
            Log.e(LOG_TAG, "Dumping Intent end")
        }
    }

    fun getCountryCode(context: Context): String {
        val ctx: Context = context
        var countryCode: String? = null
        if (ctx != null) {
            val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryCode = tm.networkCountryIso
            if (countryCode == null) {
                countryCode = tm.simCountryIso
            }
            if (countryCode != null) {
                if (countryCode.length == 2) {
                    Log.d("MAIN", "Found countrycode $countryCode")

                    return countryCode
                } else {
                    Log.e("MAIN", "countrycode length != 2")
                }
            } else {
                Log.e("MAIN", "device countrycode and sim countrycode are null")
            }
        }
        return ""
    }


}
