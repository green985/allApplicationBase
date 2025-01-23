package com.oyetech.exoplayermodule.helper

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes.Builder
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-15.11.2022-
-15:03-
 **/

class ExoPlayerRepositoryImp(
    private var exoPlayer: ExoPlayer,
    private var dispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
) : ExoPlayerBaseHelper(dispatchers, exoPlayer) {

    override fun startPlayer(radioModel: RadioStationResponseData?) {
        if (radioModel == null) {
            Timber.d("some errorooror")
            return
        }

        currentRadioModel = radioModel

        CoroutineScope(dispatchers.main).launch {
            exoPlayer.setAudioAttributes(
                Builder().setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA).build(), false
            )
        }


        playExoPlayerWithRadioModel()
    }

    override fun startPlayerForAlarm(radioModel: RadioStationResponseData?) {

        if (radioModel == null) {
            Timber.d("some errorooror")
            return
        }

        currentRadioModel = radioModel

        playExoPlayerWithRadioModel()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun resumePlayer() {
        Timber.d("player State === " + exoPlayer.playbackState)


        if (exoPlayer.playbackState != Player.STATE_IDLE) {
            exoPlayer.play()
        } else {
            currentRadioModel = radioOperationUseCase.lastStation ?: return
            startPlayer(currentRadioModel)
        }
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
    }

    override fun pause() {
        exoPlayer.pause()
    }

}