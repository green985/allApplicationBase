package com.oyetech.models.entity.general

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.BR
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-4.06.2022-
-15:43-
 **/
@Parcelize
@Json(ignore = true)
open class IsOnlineBaseData(
    @Transient
    var _isOnline: Boolean = false,
    @Transient
    var baseUserId: Long = 0L
) : BaseObservable(), Parcelable {

    @Json(ignore = true)
    var isOnlineView: Boolean
        @Bindable get() = _isOnline
        set(value) {
            _isOnline = value

            notifyPropertyChanged(BR.onlineView)
        }
}
