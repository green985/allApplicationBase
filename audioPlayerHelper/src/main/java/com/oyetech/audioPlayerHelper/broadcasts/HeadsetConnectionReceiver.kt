package com.oyetech.audioPlayerHelper.broadcasts

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.oyetech.models.radioModels.PauseReason
import timber.log.Timber

/**
Created by Erdi Özbek
-21.11.2022-
-18:09-
 **/

class HeadsetConnectionReceiver : BaseBroadcastReceiver() {

    private var headsetConnected: Boolean? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (contentOperationUseCase.getPlayerPauseReason() !== PauseReason.BECAME_NOISY) {
            return
        }
        val resumeOnWiredHeadset = sharedOperationUseCase.autoResumeOnWiredHeadsetConnection()
        val resumeOnBluetoothHeadset = sharedOperationUseCase.autoResumeOnBluetoothA2dpConnection()
        if (!resumeOnWiredHeadset && !resumeOnBluetoothHeadset) {
            return
        }
        if (contentOperationUseCase.isPlaying()) {
            return
        }
        var play = false
        if (AudioManager.ACTION_HEADSET_PLUG == intent.action) {
            if (resumeOnWiredHeadset) {
                val state = intent.getIntExtra("state", 0)
                Timber.d("stateeee = " + state)
                play = state == 1 && headsetConnected === java.lang.Boolean.FALSE
                headsetConnected = state == 1
            }
        } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED == intent.action || BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED == intent.action) {
            if (resumeOnBluetoothHeadset) {
                val state = intent.getIntExtra(
                    BluetoothProfile.EXTRA_STATE,
                    BluetoothProfile.STATE_DISCONNECTED
                )
                play =
                    state == BluetoothProfile.STATE_CONNECTED && headsetConnected === java.lang.Boolean.FALSE
                headsetConnected = state == BluetoothProfile.STATE_CONNECTED
            }
        }
        if (play) {
            contentOperationUseCase.startLastRadio()
            /*
            val radioDroidApp: RadioDroidApp = context.applicationContext as RadioDroidApp
            val historyManager: HistoryManager = radioDroidApp.getHistoryManager()
            val lastStation: DataRadioStation = historyManager.getFirst()
            if (lastStation != null) {
                if (!PlayerServiceUtil.isPlaying() && !radioDroidApp.getMpdClient()
                        .isMpdEnabled()
                ) {
                    Utils.playAndWarnIfMetered(radioDroidApp, lastStation, PlayerType.RADIODROID) {
                        Utils.play(
                            radioDroidApp,
                            lastStation
                        )
                    }
                }
            }

             */
        }
    }
}