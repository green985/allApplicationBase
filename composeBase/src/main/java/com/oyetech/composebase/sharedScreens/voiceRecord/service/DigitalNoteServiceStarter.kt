package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils.isServiceConnect
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils.serviceConnection
import com.oyetech.radioservice.serviceUtils.ServiceConst
import timber.log.Timber

object DigitalNoteServiceStarter {
    fun start(context: Context, mainActivityClass: Class<out Activity>) {
        val intent = Intent(context, DigitalNoteForegroundService::class.java).apply {
            putExtra("main_activity_class", mainActivityClass.name)
        }
        context.startForegroundService(intent)


        return
        serviceConnection = getServiceConnectionn()

        Timber.d("Start service calleddd")
        val anIntent = Intent(PlayerServiceUtils.context, DigitalNoteForegroundService::class.java)
        anIntent.putExtra(ServiceConst.PLAYER_SERVICE_NO_NOTIFICATION_EXTRA, true)
        context.bindService(
            anIntent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )


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

    fun stop(context: Context) {
        val intent = Intent(context, DigitalNoteForegroundService::class.java)
        context.stopService(intent)
    }
}