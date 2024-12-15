package com.oyetech.helper.volumeHelper

import android.content.Context
import android.media.AudioManager
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.VolumeHelperRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-17.12.2022-
-02:02-
 **/

class VolumeHelperRepositoryImp(
    private var dispatchers: AppDispatchers,
    private var context: Context,
) : VolumeHelperRepository {

    private var audioManager: AudioManager? = null

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun volumeUp() {
        audioManager?.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

    override fun volumeDown() {
        audioManager?.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);

    }

    override fun volumeMaxWithDelay(delayLong: Long) {
        var maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)

        if (maxVolume == null) {
            Timber.d("do nothingggg")
            return
        }
        GlobalScope.launch(dispatchers.io) {
            while (currentVolume == maxVolume) {
                delay(delayLong)
                volumeUp()
                currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
            }
        }

    }

}