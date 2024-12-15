package com.oyetech.models.wallpaperModels.responses

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class WallpaperSearchResponseData(
    @Json(name = "data") val wallpaperData: List<WallpaperResponseData>,
    @Json(name = "meta") val meta: MetaResponseData,
) : Parcelable
