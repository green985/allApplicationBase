package com.oyetech.models.entity.contentProperties

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-25.10.2023-
-15:27-
 **/

@Parcelize
data class ContentWrapperClass(
    var audioDurationModel: AudioDurationModel? = null,
    var contentDetailModel: OldContentPlayerDetailsModel,
) : Parcelable

fun ContentWrapperClass?.makeContentWrapperModel(
    audioDurationModel: AudioDurationModel,
    contentDetailModel: OldContentPlayerDetailsModel,
): ContentWrapperClass {

    return ContentWrapperClass(audioDurationModel, contentDetailModel)
}