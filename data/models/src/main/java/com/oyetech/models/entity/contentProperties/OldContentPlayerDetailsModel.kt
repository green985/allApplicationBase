package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.entity.contentProperties.ContentMediaTypeEnum.RADIO
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-23.10.2023-
-15:58-
 **/

@Keep
@Parcelize
data class OldContentPlayerDetailsModel(
    var _contentName: String = "",

    // content playing title, desc etc
    var _contentTitle: String = "",
    var streamUrl: String = "",
    var contentIcon: String = "",
    var _contentType: ContentMediaTypeEnum = ContentMediaTypeEnum.IDLE,

) : Parcelable,
    BaseObservable() {

    var contentName: String
        @Bindable get() = _contentName
        set(value) { _contentName = value }

    var contentTitle: String
        @Bindable get() = _contentTitle
        set(value) { _contentTitle = value }

    var contentType: ContentMediaTypeEnum
        @Bindable get() = _contentType
        set(value) { _contentType = value }

    companion object {

        fun makeContentPlayerDetail(
            streamUrl: String,
            contentName: String,
            contentTitle: String,
            contentType: ContentMediaTypeEnum,
        ): OldContentPlayerDetailsModel {
            return OldContentPlayerDetailsModel(
                streamUrl = streamUrl,
                _contentName = contentName,
                _contentTitle = contentTitle,
                _contentType = RADIO
            )
        }
    }
}
