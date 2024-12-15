package com.oyetech.audioPlayerHelper.managers

import android.content.Context
import android.media.AudioManager
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-22.11.2022-
-01:57-
 **/

class AudioManagerHelper {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    lateinit var audioManager: AudioManager

    private fun prepareAudioManagerAndGet() {
        audioManager =
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)


    }
}