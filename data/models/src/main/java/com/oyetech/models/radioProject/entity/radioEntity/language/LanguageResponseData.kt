package com.oyetech.models.radioProject.entity.radioEntity.language

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class LanguageResponseData(
    @Json(name = "iso_639")
    var iso639: String = "",
    @Json(name = "name")
    var languageName: String = "",
    @Json(name = "stationcount")
    var stationcount: Int = 0,
) : Parcelable