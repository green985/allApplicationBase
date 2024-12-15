package com.oyetech.audioPlayerHelper.mediaSessions

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import com.oyetech.domain.useCases.contentOperations.ContentOperationUseCase
import com.oyetech.models.radioModels.PauseReason
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-20.11.2022-
-21:58-
 **/

class MediaSessionCallback : MediaSessionCompat.Callback() {

    val contentOperationUseCase: ContentOperationUseCase by KoinJavaComponent.inject(
        ContentOperationUseCase::class.java
    )

    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        Timber.d("onMediaButtonEvent")

        val event = mediaButtonEvent!!.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        return if (event!!.keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            if (event.action == KeyEvent.ACTION_UP && !event.isLongPress) {
                try {
                    Timber.d("onMediaButtonEvent eventttt")
                    if (contentOperationUseCase.isPlaying()) {
                        contentOperationUseCase.pausePlayer(PauseReason.USER)
                    } else {
                        contentOperationUseCase.resumePlayer()
                    }

                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            true
        } else {
            super.onMediaButtonEvent(mediaButtonEvent)
        }
    }

    override fun onPlay() {
        super.onPlay()
        Timber.d("MediaSessionCallback == onPlay")
        contentOperationUseCase.resumePlayer()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        Timber.d("MediaSessionCallback == onPlayFromMediaId")

    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        Timber.d("MediaSessionCallback == onPlayFromSearch")

    }

    override fun onPause() {
        super.onPause()
        contentOperationUseCase.pausePlayer(PauseReason.USER)
        Timber.d("MediaSessionCallback == onPause")
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        contentOperationUseCase.nextStationRadioChannel()
        Timber.d("MediaSessionCallback == onSkipToNext")
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        contentOperationUseCase.previousRadioChannel()
        Timber.d("MediaSessionCallback == onSkipToPrevious")
    }

    override fun onStop() {
        super.onStop()
        contentOperationUseCase.stopPlayer()
        Timber.d("MediaSessionCallback == onStop")
    }
}