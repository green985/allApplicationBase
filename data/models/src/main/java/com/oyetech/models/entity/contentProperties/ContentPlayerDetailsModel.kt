package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import androidx.annotation.Keep
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

        fun makeContentDetailStateWrapper(
            ContentPlayerDetailsModel: ContentPlayerDetailsModel,
            playState: PlayState,
            errorMessage: String? = null,
        ): ContentStateView {
            return ContentStateView(playState, ContentPlayerDetailsModel)
        }
    }
}
