package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.BR
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.oyetech.models.entity.contentProperties.ContentMediaTypeEnum.BIBLE
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

    var bibleAudioPropertyResponseData: BibleAudioPropertyResponseData? = null,
) : Parcelable,
    BaseObservable() {

    var contentName: String
        @Bindable get() = _contentName
        set(value) {
            _contentName = value
            notifyPropertyChanged(BR.contentName)
        }

    var contentTitle: String
        @Bindable get() = _contentTitle
        set(value) {
            _contentTitle = value
            notifyPropertyChanged(BR.contentTitle)
        }

    var contentType: ContentMediaTypeEnum
        @Bindable get() = _contentType
        set(value) {
            _contentType = value
            notifyPropertyChanged(BR.contentType)
        }

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

        fun makeContentPlayerDetailWithBibleAudioData(
            bibleAudioPropertyResponseData: BibleAudioPropertyResponseData,
            contentName: String,
            selectedAccent: String,
        ): OldContentPlayerDetailsModel? {
            var audioFileDetailResponseData =
                bibleAudioPropertyResponseData.audioFileDetailList.find {
                    it.accent == selectedAccent
                }

            if (audioFileDetailResponseData == null) {
                return null
            }



            return OldContentPlayerDetailsModel(
                streamUrl = audioFileDetailResponseData.fileUrl,
                _contentName = contentName,
                _contentType = BIBLE,
                bibleAudioPropertyResponseData = bibleAudioPropertyResponseData
            )
        }
    }


}
