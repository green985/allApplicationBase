package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.app.Activity
import android.content.Context
import android.content.Intent

object DigitalNoteServiceStarter {
    fun start(context: Context, mainActivityClass: Class<out Activity>) {
        val intent = Intent(context, DigitalNoteForegroundService::class.java).apply {
            putExtra("main_activity_class", mainActivityClass.name)
        }
        context.startForegroundService(intent)


        return


    }

    fun stop(context: Context) {
        val intent = Intent(context, DigitalNoteForegroundService::class.java)
        context.stopService(intent)
    }
}