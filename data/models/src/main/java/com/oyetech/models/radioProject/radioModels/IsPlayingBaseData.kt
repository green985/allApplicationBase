package com.oyetech.models.radioProject.radioModels

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.room.Ignore
import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Json(ignore = true)
open class IsPlayingBaseData(
    @Ignore
    @IgnoredOnParcel
    @Transient
    var _isPlaying: Boolean = false,
    @Ignore
    @IgnoredOnParcel
    @Transient
    var _isFavoriteView: Boolean = false,
    @Ignore
    @IgnoredOnParcel
    @Transient
    var _isSelectedView: Boolean = false,
    @Ignore
    @IgnoredOnParcel
    @Transient
    var radioStationUUID: String = "",
) : BaseObservable(), Parcelable {

    /*

    @Json(ignore = true)
    var isPlayingView: Boolean
        @Bindable get() = _isPlaying
        set(value) {
            _isPlaying = value

            notifyPropertyChanged(BR.playingView)
        }

    @Json(ignore = true)
    var isFavoriteView: Boolean
        @Bindable get() = _isFavoriteView
        set(value) {
            _isFavoriteView = value

            notifyPropertyChanged(BR.favoriteView)
        }

    @Json(ignore = true)
    var isSelectedView: Boolean
        @Bindable get() = _isSelectedView
        set(value) {
            _isSelectedView = value

            notifyPropertyChanged(BR.selectedView)
        }

     */
}
