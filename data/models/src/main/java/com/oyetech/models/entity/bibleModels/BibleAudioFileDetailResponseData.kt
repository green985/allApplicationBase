package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-25.11.2023-
-13:10-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleAudioFileDetailResponseData(

    @Json(name = "accent")
    var accent: String = "default",

    @Json(name = "fileUrl")
    var fileUrl: String = "",
) : Parcelable