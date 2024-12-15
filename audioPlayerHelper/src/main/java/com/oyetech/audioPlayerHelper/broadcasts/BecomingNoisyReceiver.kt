package com.oyetech.audioPlayerHelper.broadcasts

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.oyetech.models.radioModels.PauseReason

class BecomingNoisyReceiver : BaseBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BecomingNoisyReceiver", "onReceive: ")
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action && contentOperationUseCase.isPlaying()) {
            if (sharedOperationUseCase.autoResumeOnBluetoothA2dpConnection()) {
                Log.d("BecomingNoisyReceiver", "onReceive: BECAME_NOISY")

                contentOperationUseCase.pausePlayer(PauseReason.BECAME_NOISY);
            }
        }

    }
}