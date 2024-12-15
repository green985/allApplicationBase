package com.oyetech.models.wallpaperModels.responses

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class MetaResponseData(
    @Json(name = "current_page") val current_page: Int,
    @Json(name = "last_page") val last_page: Int,
    @Json(name = "per_page") val per_page: Int,
    @Json(name = "total") val total: Int,
) : Parcelable