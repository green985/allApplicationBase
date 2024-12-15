package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.BR
import com.oyetech.models.utils.states.TextFontEnum
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-13.11.2023-
-16:44-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleContentResponseData(
    @Json(name = "content")
    var content: String? = "",
    var _contentFontSizeMultiplier: Float,

    @IgnoredOnParcel
    var _contentFontStyle: TextFontEnum,
) : Parcelable, BaseObservable() {

    var contentFontSizeMultiplier: Float
        @Bindable get() = _contentFontSizeMultiplier
        set(value) {
            _contentFontSizeMultiplier = value
            notifyPropertyChanged(BR.contentFontSizeMultiplier)
        }
    var contentFontStyle: TextFontEnum
        @Bindable get() = _contentFontStyle
        set(value) {
            _contentFontStyle = value
            notifyPropertyChanged(BR.contentFontStyle)
        }
}