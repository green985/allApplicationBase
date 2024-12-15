package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-25.11.2023-
-12:50-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleAudioAccentPropertyResponseData(
    @Json(name = "accentName")
    var accentName: String = "",
    @Json(name = "accentCode")
    var accentCode: String = "",
) : Parcelable