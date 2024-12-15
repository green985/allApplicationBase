package com.oyetech.radioservice.broadcasts

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.oyetech.models.radioProject.radioModels.PauseReason

class BecomingNoisyReceiver : BaseBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BecomingNoisyReceiver", "onReceive: ")
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action && radioOperationUseCase.isPlaying()) {
            if (sharedOperationUseCase.autoResumeOnBluetoothA2dpConnection()) {
                Log.d("BecomingNoisyReceiver", "onReceive: BECAME_NOISY")

                radioOperationUseCase.pausePlayer(PauseReason.BECAME_NOISY);
            }
        }

    }
}