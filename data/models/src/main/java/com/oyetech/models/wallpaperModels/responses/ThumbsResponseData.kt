package com.oyetech.models.wallpaperModels.responses

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class ThumbsResponseData(
    @Json(name = "large") val large: String,
    @Json(name = "original") val original: String,
    @Json(name = "small") val small: String,
) : Parcelable