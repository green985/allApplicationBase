package com.oyetech.radioservice.helper

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.PauseReason.FOCUS_LOSS
import com.oyetech.models.radioProject.radioModels.PauseReason.FOCUS_LOSS_TRANSIENT
import com.oyetech.radioservice.BuildConfig
import com.oyetech.radioservice.serviceUtils.ServiceConst
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-22.11.2022-
-01:01-
 **/

class AudioFocusChangeListener : OnAudioFocusChangeListener {

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )

    val context: Context by KoinJavaComponent.inject(Context::class.java)

    override fun onAudioFocusChange(focusChange: Int) {
        Timber.d("onAudioFocusChange == " + focusChange)
        listener.onAudioFocusChange(focusChange)
    }

    var listener = object : OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            val pauseReason = radioOperationUseCase.getPlayerPauseReason()
            /*
            if (!radioPlayer.isLocal()) {
                return
            }
            
             */
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (context.isDebug()) Timber.d("audio focus gain")
                    if (pauseReason === FOCUS_LOSS_TRANSIENT) {
                        radioOperationUseCase.resumePlayer()
                    }
                    radioOperationUseCase.setVolume(ServiceConst.FULL_VOLUME)
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    if (context.isDebug()) Timber.d("audio focus loss")
                    if (radioOperationUseCase.isPlaying()) {
                        radioOperationUseCase.pausePlayer(FOCUS_LOSS)
                    }
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (context.isDebug()) Timber.d("audio focus loss transient")
                    if (radioOperationUseCase.isPlaying()) {
                        radioOperationUseCase.pausePlayer(FOCUS_LOSS_TRANSIENT)
                    }
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    if (BuildConfig.DEBUG) Timber.d("audio focus loss transient can duck")
                    radioOperationUseCase.setVolume(ServiceConst.DUCK_VOLUME)
                }
            }

        }
    }


}