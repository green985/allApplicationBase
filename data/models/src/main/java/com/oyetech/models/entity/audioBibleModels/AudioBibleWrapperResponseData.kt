package com.oyetech.models.entity.audioBibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioBibleWrapperResponseData(
    @Json(name = "data")
    var `data`: AudioBibleResponseData? = AudioBibleResponseData(),
) : Parcelable