package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VolumeDoublePressReceiver : BroadcastReceiver(), VolumeDoublePressListener {
    override fun onReceive(context: Context, intent: Intent) {}

    override fun onVolumeDoublePress(type: VolumeKeyType) {}
}
