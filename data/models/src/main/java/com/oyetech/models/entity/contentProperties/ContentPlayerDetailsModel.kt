package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.oyetech.models.entity.contentProperties.ContentMediaTypeEnum.BIBLE
import com.oyetech.models.entity.contentProperties.ContentMediaTypeEnum.RADIO
import com.oyetech.models.radioProject.radioModels.ContentStateView
import com.oyetech.models.radioProject.radioModels.PlayState
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-13.11.2024-
-19:07-
 **/

@Keep
@Parcelize
data class ContentPlayerDetailsModel(
    var contentName: String = "",

    // content playing title, desc etc
    var contentTitle: String = "",
    var streamUrl: String = "",
    var contentIcon: String = "",
    var contentType: ContentMediaTypeEnum = ContentMediaTypeEnum.IDLE,

    var bibleAudioPropertyResponseData: BibleAudioPropertyResponseData? = null,
) : Parcelable {

    companion object {

        fun makeContentPlayerDetail(
            streamUrl: String,
            contentName: String,
            contentTitle: String,
            contentType: ContentMediaTypeEnum,
        ): ContentPlayerDetailsModel {
            return ContentPlayerDetailsModel(
                streamUrl = streamUrl,
                contentName = contentName,
                contentTitle = contentTitle,
                contentType = RADIO
            )
        }

        fun makeContentPlayerDetailWithBibleAudioData(
            bibleAudioPropertyResponseData: BibleAudioPropertyResponseData,
            contentName: String,
            selectedAccent: String,
        ): ContentPlayerDetailsModel? {
            var audioFileDetailResponseData =
                bibleAudioPropertyResponseData.audioFileDetailList.find {
                    it.accent == selectedAccent
                }

            if (audioFileDetailResponseData == null) {
                return null
            }



            return ContentPlayerDetailsModel(
                streamUrl = audioFileDetailResponseData.fileUrl,
                contentName = contentName,
                contentType = BIBLE,
                bibleAudioPropertyResponseData = bibleAudioPropertyResponseData
            )
        }

        fun makeContentDetailStateWrapper(
            ContentPlayerDetailsModel: ContentPlayerDetailsModel,
            playState: PlayState,
            errorMessage: String? = null,
        ): ContentStateView {
            return ContentStateView(playState, ContentPlayerDetailsModel)

        }
    }


}
