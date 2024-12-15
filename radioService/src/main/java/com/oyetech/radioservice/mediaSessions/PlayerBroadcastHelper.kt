package com.oyetech.radioservice.mediaSessions

import android.app.Service
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.oyetech.core.ext.doInTryCatchWithoutStack
import com.oyetech.radioservice.broadcasts.BecomingNoisyReceiver
import com.oyetech.radioservice.broadcasts.HeadsetConnectionReceiver

open class PlayerBroadcastHelper(private var service: Service) {

    private val headsetConnectionReceiver = HeadsetConnectionReceiver()
    private val becomingNoisyReceiver: BecomingNoisyReceiver = BecomingNoisyReceiver()

    fun registerAllBroadcast() {
        registerBecomingNoisyBroadCastReceiver()
        registerHeadsetConnectionBroadCast()
    }

    fun unRegisterAllBroadcast() {
        doInTryCatchWithoutStack {
            unRegisterHeadsetConnectionBroadCast()
        }
        doInTryCatchWithoutStack {
            unRegisterBecomingNoisyBroadCastReceiver()
        }
    }

    private fun registerHeadsetConnectionBroadCast() {
        val headsetConnectionFilter = IntentFilter()
        headsetConnectionFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        headsetConnectionFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        headsetConnectionFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)

        service.registerReceiver(headsetConnectionReceiver, headsetConnectionFilter)
    }

    private fun unRegisterHeadsetConnectionBroadCast() {
        service.unregisterReceiver(headsetConnectionReceiver)
    }

    private fun registerBecomingNoisyBroadCastReceiver() {
        val becomingNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        service.registerReceiver(becomingNoisyReceiver, becomingNoisyFilter)
    }

    private fun unRegisterBecomingNoisyBroadCastReceiver() {
        service.unregisterReceiver(becomingNoisyReceiver)
    }

}
