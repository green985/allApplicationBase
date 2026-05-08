package com.oyetech.exoplayermodule.helper

import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes.Builder
import com.google.android.exoplayer2.source.MediaSource
import com.oyetech.domain.repository.contentOperation.ExoPlayerOperationRepository
import com.oyetech.exoplayermodule.analytics.ExoplayerAnalyticsListener
import com.oyetech.exoplayermodule.utils.urlIndicatesHlsStream
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi zbek
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
        exoPlayer.addAnalyticsListener(exoplayerAnalyticsListener)
        exoPlayer.addListener(this)

        exoPlayer.setAudioAttributes(
            Builder().setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA).build(),
            false
        )
    }

    fun createMediaSource(isHlsProblem: Boolean = false): MediaSource {
        val streamUrl = currentRadioModel.radioStreamUrl
        var isHls = urlIndicatesHlsStream(streamUrl)
        if (isHlsProblem) {
            isHls = !isHls
        }

        val mediaItem = MediaItem.fromUri(streamUrl)
        val audioSource: MediaSource

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
                .setUsage(C.USAGE_ALARM).build(),
            false
        )
    }

    fun playExoPlayerWithRadioModel(isHlsProblem: Boolean = false, isAlarm: Boolean = false) {
        CoroutineScope(dispatchers.main).launch {
            exoPlayer.stop()

            val mediaSource = createMediaSource(isHlsProblem)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            exoPlayer.playWhenReady = true
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        val radioTitle = mediaMetadata.title.toString()
        Timber.d("media dataaa === $radioTitle")
        Timber.d("media dataaa === $mediaMetadata")
    }

    override fun onPlayerStateChanged(isLoading: Boolean, playbackState: Int) {
        Log.d("onPlayerStateChanged", "onPlayerStateChanged=$playbackState")

        isPlayingFlag =
            playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING
    }

    // TODO: Refactor — wire these to a new state manager after RadioOperationUseCase removal
    fun changeRadioState(state: Any) {
        Timber.d("changeRadioState: $state (no-op until refactored)")
    }

    fun changeRadioTitle(radioTitle: String?) {
        Timber.d("changeRadioTitle: $radioTitle (no-op until refactored)")
    }

    fun changeRadioErrorState(errorString: String) {
        Timber.d("changeRadioErrorState: $errorString (no-op until refactored)")
    }

    override fun onPlayerError(error: PlaybackException) {
        val errorCode = error.errorCode
        Timber.d("errororororr == $errorCode")
        if (errorCode == 0) {
            playExoPlayerWithRadioModel(isHlsProblem = true)
        } else {
            stop()
            Timber.d("RadioError%s", error.message)
        }
    }
}
