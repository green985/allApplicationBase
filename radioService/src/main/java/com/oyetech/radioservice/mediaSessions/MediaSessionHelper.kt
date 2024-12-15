package com.oyetech.radioservice.mediaSessions

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.net.wifi.WifiManager.WifiLock
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.Builder
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.oyetech.core.contextHelper.getMainActivityStartIntent
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.PauseReason
import com.oyetech.models.radioProject.radioModels.PauseReason.NONE
import com.oyetech.models.radioProject.radioModels.PlayState
import com.oyetech.models.radioProject.radioModels.PlayState.Idle
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import com.oyetech.models.radioProject.radioModels.PlayState.PrePlaying
import com.oyetech.models.radioProject.radioModels.RadioViewStateNew
import com.oyetech.models.utils.const.NotificationConst.NOTIFY_ID
import com.oyetech.radioservice.R
import com.oyetech.radioservice.R.string
import com.oyetech.radioservice.notificationHelper.RadioNotificationHelper
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-20.11.2022-
-21:55-
 **/

class MediaSessionHelper(private var service: Service) : PlayerBroadcastHelper(service) {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )

    private val pauseReason = NONE

    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = MediaSessionCallback()

    lateinit var notificationHelper: RadioNotificationHelper

    var powerManager: PowerManager? = null
    var wakeLock: WakeLock? = null
    var wifiLock: WifiLock? = null

    private val lastErrorFromPlayer = -1

    init {
        prepareMediaSession()
    }

    fun getMediaSession(): MediaSessionCompat {
        if (!::mediaSession.isInitialized) {
            prepareMediaSession()
            return mediaSession
        }
        return mediaSession
    }

    private fun prepareMediaSession() {

        mediaSession = MediaSessionCompat(context, context.packageName)
        mediaSession.setCallback(mediaSessionCallback)

        val startActivityIntent = context.getMainActivityStartIntent()

        mediaSession.setSessionActivity(
            PendingIntent.getActivity(
                context,
                0,
                startActivityIntent,
                PlayerServiceUtils.getIntentFlagUpdateWithInMutable()
            )
        )

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        notificationHelper = RadioNotificationHelper(service, mediaSession)

    }

    fun fireAndStopDummyNotification() {
        notificationHelper.fireAndStopDummyNotification()
    }

    fun enableMediaSession() {
        if (context.isDebug()) Log.d("MEdiaSessionnnn", "enabling media session.")

        registerAllBroadcast()
        mediaSession.isActive = true
        setMediaPlaybackState(PlaybackStateCompat.STATE_NONE)
    }

    fun disableMediaSession() {
        if (mediaSession.isActive) {
            if (context.isDebug()) Log.d("MEdiaSessionnnn", "disabling media session.")
            mediaSession.isActive = false
        }
        unRegisterAllBroadcast()
    }

    private fun setMediaPlaybackState(state: Int) {
        if (mediaSession == null) {
            return
        }
        var actions = (PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_PLAY_PAUSE)
        actions =
            if (state == PlaybackStateCompat.STATE_BUFFERING || state == PlaybackStateCompat.STATE_PLAYING) {
                actions or PlaybackStateCompat.ACTION_PAUSE
            } else {
                actions or PlaybackStateCompat.ACTION_PLAY
            }
        val playbackStateBuilder = Builder()
        playbackStateBuilder.setActions(actions)
        if (state == PlaybackStateCompat.STATE_ERROR) {
            var error = ""

            val currentPlayerState: PlayState = radioOperationUseCase.getPlayerState()
            if ((currentPlayerState === PlayState.Paused || currentPlayerState === PlayState.Idle)
                && pauseReason === PauseReason.METERED_CONNECTION
            ) {
                error = context.getResources().getString(R.string.notify_metered_connection)
            } else {
                try {
                    error = "ERROR"
                } catch (ex: NotFoundException) {
                    // Log.e("MediaSessionnnnnn", String.format("Unknown play error: %d", lastErrorFromPlayer), ex)
                    Log.e(
                        "MediaSessionnnnnn",
                        String.format("Unknown play error: %s", "mediasdakdmaksdn "),
                        ex
                    )
                }
            }
            playbackStateBuilder.setErrorMessage(
                PlaybackStateCompat.ERROR_CODE_ACTION_ABORTED,
                error
            )
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mediaSession.setPlaybackState(playbackStateBuilder.build())
    }

    fun updateNotification(radioModel: RadioViewStateNew) {
        Timber.e("updateNotification === " + radioModel.status)
        var playState = radioModel.status
        var radioDataModel = radioModel.data
        if (radioDataModel == null) {
            Timber.e("Radio model cannot be nullllll")
            return
        }
        var currentStationName = radioDataModel.radioName ?: ""

        when (playState) {
            Idle -> {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(NOTIFY_ID)
                setMediaPlaybackState(PlaybackStateCompat.STATE_NONE)
            }

            PrePlaying -> {
                notificationHelper.sendMessage(
                    theTitle = currentStationName,
                    theMessage = context.getResources().getString(string.notify_pre_play),
                    theTicker = context.getResources().getString(
                        string.notify_pre_play
                    ),
                    radioLogo = radioDataModel.favicon
                )

                setMediaPlaybackState(PlaybackStateCompat.STATE_BUFFERING)
            }

            Playing -> {
                // val title: String = liveInfo.getTitle()
                val title: String = radioDataModel.radioTitle
                if (!TextUtils.isEmpty(title) || title != "null") {
                    if (context.isDebug()) Log.d("MEdiaSessionnnn", "update message:$title")
                    notificationHelper.sendMessage(
                        theTitle = currentStationName,
                        theMessage = title,
                        theTicker = title,
                        radioLogo = radioDataModel.favicon
                    )
                } else {
                    notificationHelper.sendMessage(
                        theTitle = currentStationName,
                        theMessage = context.getResources().getString(string.notify_play),
                        theTicker = currentStationName,
                        radioLogo = radioDataModel.favicon
                    )
                }
                if (mediaSession != null) {
                    val builder = MediaMetadataCompat.Builder()
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentStationName)
                    /*
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, liveInfo.getArtist())
                    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, liveInfo.getTrack())
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentStation.Name)
                    if (liveInfo.hasArtistAndTrack()) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_ARTIST,
                            liveInfo.getArtist()
                        )
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_TITLE,
                            liveInfo.getTrack()
                        )
                    } else {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_TITLE,
                            liveInfo.getTitle()
                        )
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_ARTIST,
                            currentStation.Name
                        ) // needed for android-media-controller to show an icon
                    }
                    builder.putBitmap(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
                        radioIcon.getBitmap()
                    )
                    builder.putBitmap(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        radioIcon.getBitmap()
                    )
                     */

                    mediaSession.setMetadata(builder.build())
                }
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }

            Paused -> {
                notificationHelper.sendMessage(
                    theTitle = currentStationName,
                    theMessage = context.getResources().getString(string.notify_paused),
                    theTicker = currentStationName,
                    radioDataModel.favicon
                )
                if (lastErrorFromPlayer != -1) {
                    setMediaPlaybackState(PlaybackStateCompat.STATE_ERROR)
                } else {
                    setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                }
            }
        }
    }

    fun releaseMediaSession() {
        disableMediaSession()
        mediaSession.release()
    }
}