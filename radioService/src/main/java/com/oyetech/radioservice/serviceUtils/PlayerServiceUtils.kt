package com.oyetech.radioservice.serviceUtils

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.util.Log
import com.oyetech.radioservice.services.PlayerService
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-20.11.2022-
-16:31-
 **/

object PlayerServiceUtils {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    var isServiceConnect = false

    var serviceConnection: ServiceConnection? = null

    fun startService() {
        if (isServiceConnect) return
        isServiceConnect = true

        Timber.d("Start service calleddd")
        val anIntent = Intent(context, PlayerService::class.java)
        anIntent.putExtra(ServiceConst.PLAYER_SERVICE_NO_NOTIFICATION_EXTRA, true)
        serviceConnection = getServiceConnectionn()

        context?.bindService(
            anIntent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )

    }

    fun bindService() {
        if (isServiceConnect) return
        isServiceConnect = true

        Timber.d("bindService calleddd")
        val anIntent = Intent(context, PlayerService::class.java)
        serviceConnection = getServiceConnectionn()
        context.bindService(
            anIntent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )

    }

    fun stopService() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection!!)
        }
        serviceConnection = null
        isServiceConnect = false
    }

    private fun getServiceConnectionn(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                Log.d("PLAYER", "Service came online")

                isServiceConnect = true
            }

            override fun onServiceDisconnected(className: ComponentName) {
                // dont use that...

                Log.d("PLAYER", "Service offline")
                isServiceConnect = false
            }
        }
    }

    fun getIntentFlagUpdateWithInMutable(): Int {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            return PendingIntent.FLAG_UPDATE_CURRENT
        }
    }


}