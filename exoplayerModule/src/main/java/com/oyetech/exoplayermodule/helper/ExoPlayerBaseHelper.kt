package com.oyetech.exoplayermodule.helper

import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.audio.AudioAttributes.Builder
import com.google.android.exoplayer2.source.MediaSource
import com.oyetech.domain.repository.contentOperation.ExoPlayerOperationRepository
import com.oyetech.exoplayermodule.analytics.ExoplayerAnalyticsListener
import com.oyetech.exoplayermodule.utils.urlIndicatesHlsStream
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PlayState
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.radioProject.radioModels.PlayState.PrePlaying
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-15.11.2022-
-17:16-
 **/

abstract class ExoPlayerBaseHelper(
    private var dispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private var exoPlayer: ExoPlayer,
) : ExoPlayerDiHelper(),
    ExoPlayerOperationRepository,
    Player.Listener {

    val exoplayerAnalyticsListener: ExoplayerAnalyticsListener by KoinJavaComponent.inject(
        ExoplayerAnalyticsListener::class.java
    )

    var isPlayingFlag: Boolean = false

    var currentRadioModel = RadioStationResponseData()
        set(value) {
            exoplayerAnalyticsListener.lastStationResponseData = field
            field = value
        }

    init {
        CoroutineScope(dispatchers.main).launch {
            prepareExoPlayerProperty()
        }
        exoplayerAnalyticsListener.lastStationResponseData = currentRadioModel
    }

    private fun prepareExoPlayerProperty() {
        var isAlarm = false

        exoPlayer.addAnalyticsListener(exoplayerAnalyticsListener)
        exoPlayer.addListener(this)

        exoPlayer.setAudioAttributes(
            Builder().setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(if (isAlarm) C.USAGE_ALARM else C.USAGE_MEDIA).build(), false
        )
    }

    fun createMediaSource(isHlsProblem: Boolean = false): MediaSource {
        var streamUrl = currentRadioModel.radioStreamUrl
        var isHls = urlIndicatesHlsStream(streamUrl)
        if (isHlsProblem) {
            isHls = !isHls
        }

        var mediaItem = MediaItem.fromUri(streamUrl)
        var audioSource: MediaSource

        if (isHls) {
            audioSource = hlsMediaSource
                .setLoadErrorHandlingPolicy(getCustomErrorHandlingPolicyy())
                .createMediaSource(mediaItem)
        } else {
            audioSource = progressiveMediaSource
                .setLoadErrorHandlingPolicy(getCustomErrorHandlingPolicyy())
                .createMediaSource(mediaItem)
        }

        return audioSource
    }

    fun setExoPlayerAlarmMode() {
        exoPlayer.setAudioAttributes(
            Builder().setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_ALARM).build(), false
        )
    }

    fun playExoPlayerWithRadioModel(isHlsProblem: Boolean = false, isAlarm: Boolean = false) {
        changeRadioState(PrePlaying)
        CoroutineScope(dispatchers.main).launch {
            exoPlayer.stop()

            var mediaSource = createMediaSource(isHlsProblem)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            exoPlayer.playWhenReady = true
        }

    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super<Listener>.onMediaMetadataChanged(mediaMetadata)
        var radioTitle = mediaMetadata.title.toString()
        Timber.d("media dataaa === " + radioTitle)

        if (radioTitle.isNotBlank() && radioTitle != "null") {
            changeRadioTitle(radioTitle)
        }
        Timber.d("media dataaa === " + mediaMetadata)
    }

    override fun onPlayerStateChanged(isLoading: Boolean, playbackState: Int) {
        Log.d("onPlayerStateChanged", "onPlayerStateChanged=$playbackState")

        isPlayingFlag =
            playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING

        when (playbackState) {
            Player.STATE_READY -> {
                changeRadioState(PlayState.Playing)
            }

            Player.STATE_BUFFERING -> {
                changeRadioState(PlayState.PrePlaying)
            }
        }
        /*
        when (playbackState) {
            Player.STATE_IDLE -> {
                radioViewState.value = RadioViewState.stop(RadioStationResponseData)
            }
            Player.STATE_BUFFERING -> {
                radioViewState.value = RadioViewState.loading(RadioStationResponseData)
            }
            Player.STATE_ENDED -> {
                //Player destroy
                radioViewState.value = RadioViewState.stop(RadioStationResponseData)
            }
            Player.STATE_READY -> {
                // radio start in here
                if (exoPlayer.playWhenReady) {
                    radioViewState.value = RadioViewState.playing(RadioStationResponseData)
                } else {
                    radioViewState.value = RadioViewState.stop(RadioStationResponseData)
                }
            }
        }

         */
    }

    fun changeRadioState(state: PlayState) {
        radioOperationUseCase.setRadioViewStateData(state)
    }

    fun changeRadioTitle(radioTitle: String?) {
        radioOperationUseCase.setRadioTitleData(radioTitle)
    }

    fun changeRadioErrorState(errorString: String) {
        radioOperationUseCase.changeRadioErrorState(errorString)
    }

    override fun onPlayerError(error: PlaybackException) {
        var errorCode = error.errorCode
        Timber.d("errororororr == " + errorCode)
        if (errorCode == 0) {
            //is Error cause for mediaSource
            //Try to play again. Type.TYPE_SOURCE = 0
            playExoPlayerWithRadioModel(isHlsProblem = true)
        } else {
            stop()
            changeRadioState(Paused)
            changeRadioErrorState("error")
            Timber.d("RadioError%s", error.message)
        }
    }
}