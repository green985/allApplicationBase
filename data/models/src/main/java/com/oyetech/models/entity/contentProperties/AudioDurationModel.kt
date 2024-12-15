package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Ignore
import com.oyetech.models.BR
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.utils.helper.TimeFunctions
import com.oyetech.models.utils.states.AUDIO_IDLE
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-10.04.2022-
-19:25-
 **/

@Parcelize
data class AudioDurationModel(
    var _totalDuration: Long = 0L,
    var _playerAudioState: Int = AUDIO_IDLE,

    var _currentDuration: Long = 0L,

    ) : Parcelable,
    BaseObservable() {

    @Ignore
    @IgnoredOnParcel
    @Transient
    var startPlayerAction: ((MessageDetailDataResponse?) -> Unit)? = null

    @Ignore
    @IgnoredOnParcel
    @Transient
    var startPlayerCurrentAction: ((Long?) -> Unit)? = null

    @Ignore
    @IgnoredOnParcel
    @Transient
    var isTracing: Boolean = false

    var currentDuration: Long
        @Bindable get() = _currentDuration
        set(value) {
            _currentDuration = value
            if (!isTracing) {
                notifyPropertyChanged(BR.currentDuration)
            }
        }

    var totalDuration: Long
        @Bindable get() = _totalDuration
        set(value) {
            _totalDuration = value
            notifyPropertyChanged(BR.totalDuration)
        }

    var playerAudioState: Int
        @Bindable get() = _playerAudioState
        set(value) {
            _playerAudioState = value
            notifyPropertyChanged(BR.playerAudioState)
        }

    fun getTotalDurationFormatString(): String {
        if (totalDuration == 0L) {
            return ""
        } else {
            return TimeFunctions.getHourMinSecFromLongString(totalDuration)
        }
    }

    fun getCurrentDurationFormatString(): String {
        if (totalDuration == 0L) {
            return ""
        } else {
            return TimeFunctions.getHourMinSecFromLongString(currentDuration)
        }
    }

}

enum class ExoPlayerState {
    IDLE, PLAYING, STOP, ERROR, BUFFERING
}

