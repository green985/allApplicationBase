package com.oyetech.audioPlayerHelper.services

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.Parcelable
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.useCases.contentOperations.ContentOperationUseCase
import com.oyetech.models.radioModels.ContentStateView
import com.oyetech.models.radioModels.PauseReason
import com.oyetech.models.radioModels.PauseReason.FOCUS_LOSS_TRANSIENT
import com.oyetech.models.radioModels.PauseReason.USER
import com.oyetech.models.radioModels.PlayState.Idle
import com.oyetech.models.radioModels.PlayState.PrePlaying
import com.oyetech.audioPlayerHelper.binders.LocalBinder
import com.oyetech.audioPlayerHelper.helper.AudioFocusChangeListener
import com.oyetech.audioPlayerHelper.mediaSessions.MediaSessionHelper
import com.oyetech.audioPlayerHelper.serviceUtils.PlayerServiceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import org.koin.android.scope.ScopeService
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-20.11.2022-
-17:21-
 **/

abstract class PlayerServiceHelper : ScopeService() {

    var binder = LocalBinder()

    lateinit var mediaSessionHelper: MediaSessionHelper

    lateinit var audioManager: AudioManager

    var corutinesScope = CoroutineScope(Dispatchers.IO)

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val contentOperationUseCase: ContentOperationUseCase by KoinJavaComponent.inject(
        ContentOperationUseCase::class.java
    )
    var radioOperationJobs: Job? = null
    var radioPauseReasonOperationJobs: Job? = null

    private var audioFocusChangeListener = AudioFocusChangeListener()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        Timber.d("service on Destroy calledd.")
        stopService()
        super.onDestroy()

    }

    fun pause() {
        Timber.d("service pause called")
        // Pausing due to focus loss means that we can gain it again
        // so we should keep the focus and the wait for callback.

        // Pausing due to focus loss means that we can gain it again
        // so we should keep the focus and the wait for callback.

        contentOperationUseCase.pausePlayer(USER)
    }

    fun next() {
        contentOperationUseCase.nextStationRadioChannel()
    }

    fun previous() {
        contentOperationUseCase.previousRadioChannel()

    }

    private fun isCanPlay(): Boolean {
        val result: Int = acquireAudioFocus()
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaSessionHelper.enableMediaSession()
            acquireWakeLockAndWifiLock()
            return true
        }
        return false
    }

    fun playCurrent() {
        // Timber.d("service pause called")
        contentOperationUseCase.startLastRadio()
    }

    fun resume() {
        Timber.d("service resume called")
        var isPlaying = contentOperationUseCase.isPlaying()

        if (!isPlaying) {
            acquireAudioFocus()
            contentOperationUseCase.resumePlayer()
        }

    }

    fun prepareServiceInit() {
        mediaSessionHelper = MediaSessionHelper(this)
        audioManager =
            (this.getSystemService(AUDIO_SERVICE) as AudioManager)!!
    }

    fun releaseAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }

    private fun acquireAudioFocus(): Int {
        if (context.isDebug()) Timber.d("acquiring audio focus.")

        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,  // Use the music stream.
            AudioManager.STREAM_MUSIC,  // Request permanent focus.
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Timber.e("acquiring audio focus failed!")
            // toastOnUi(string.error_grant_audiofocus)
        }

        return result
    }

    fun actionMediaButton(intent: Intent) {
        Timber.d("service resume called")
        val key = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent?
        if (key!!.action == KeyEvent.ACTION_UP) {
            val keycode = key!!.keyCode
            when (keycode) {
                KeyEvent.KEYCODE_MEDIA_PLAY -> resume()
                KeyEvent.KEYCODE_MEDIA_NEXT -> contentOperationUseCase.nextStationRadioChannel()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> contentOperationUseCase.previousRadioChannel()
            }
        }
    }

    fun stopService() {
        // Stop everything about service...
        contentOperationUseCase.setPauseReason(PauseReason.NONE)
        releaseAudioFocus()
        mediaSessionHelper.releaseMediaSession()

        contentOperationUseCase.stopPlayer()
        releaseWakeLockAndWifiLock()
        PlayerServiceUtils.stopService()
        stopSelf()
        stopForeground(true)
    }

    fun getRadioOperationServiceCollector(): FlowCollector<ContentStateView> {
        var collector = object : FlowCollector<ContentStateView> {
            override suspend fun emit(value: ContentStateView) {
                if (!currentCoroutineContext().isActive) {
                    Timber.d("jobs cannecled")
                    return
                }
                Timber.d("valıeee = " + value.toString())

                if (!PlayerServiceUtils.isServiceConnect) {
                    if (value.status != Idle) {
                        PlayerServiceUtils.bindService()
                    }
                }

                setAudioFocusWhenPlayingOrStop(value)

                mediaSessionHelper.updateNotification(value)
            }
        }
        return collector
    }

    private fun setAudioFocusWhenPlayingOrStop(value: ContentStateView) {

        if (value.status == PrePlaying) {
            if (!isCanPlay()) {
                contentOperationUseCase.stopPlayer()
            }
        }
    }

    fun getRadioPauseReasonServiceCollector(): FlowCollector<PauseReason> {
        var collector = object : FlowCollector<PauseReason> {
            override suspend fun emit(value: PauseReason) {
                if (!currentCoroutineContext().isActive) {
                    Timber.d("jobs cannecled")
                    return
                }
                Timber.d("valıeee = " + value.toString())

                // Pausing due to focus loss means that we can gain it again
                // so we should keep the focus and the wait for callback.
                if (value !== FOCUS_LOSS_TRANSIENT) {
                    releaseAudioFocus()
                }

            }
        }
        return collector
    }

    private fun releaseWakeLockAndWifiLock() {
        var wakeLock = mediaSessionHelper.wakeLock
        var wifiLock = mediaSessionHelper.wifiLock
        if (context.isDebug()) Timber.d("releasing wake lock and wifi lock.")

        if (wakeLock != null) {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
            wakeLock = null
            mediaSessionHelper.wakeLock = null
        }

        if (wifiLock != null) {
            if (wifiLock.isHeld) {
                wifiLock.release()
            }
            wifiLock = null
            mediaSessionHelper.wifiLock = null
        }
    }

    private fun acquireWakeLockAndWifiLock() {
        var wakeLock = mediaSessionHelper.wakeLock
        var wifiLock = mediaSessionHelper.wifiLock
        var powerManager = mediaSessionHelper.powerManager

        if (powerManager == null) return

        var TAG = "asdadass"
        if (context.isDebug()) Timber.d("acquiring wake lock and wifi lock.")

        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PlayerService:")
        }
        if (!wakeLock!!.isHeld()) {
            wakeLock.acquire(60 * 60 * 1000L /*60 minutes*/)
        } else {
            if (context.isDebug()) Log.d(TAG, "wake lock is already acquired.")
        }

        val wm = context.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager?
        if (wm != null) {
            if (wifiLock == null) {
                wifiLock =
                    wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "PlayerService")
            }
            if (!wifiLock!!.isHeld()) {
                doInTryCatch {
                    wifiLock.acquire()
                }
            } else {
                if (context.isDebug()) Log.d(TAG, "wifi lock is already acquired.")
            }
        } else {
            Log.e(TAG, "could not acquire wifi lock, WifiManager does not exist!")
        }
    }

}