package com.oyetech.models.radioEntity.stationClick

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class StationClickResponseData(
    @Json(name = "message")
    var message: String = "",
    @Json(name = "name")
    var name: String = "",
    @Json(name = "ok")
    var ok: Boolean = false,
    @Json(name = "stationuuid")
    var stationuuid: String = "",
    @Json(name = "url")
    var url: String = ""
) : Parcelable