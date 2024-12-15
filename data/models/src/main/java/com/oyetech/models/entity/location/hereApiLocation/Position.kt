package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Position(
    @Json(name = "lat")
    var lat: Double? = 0.0,
    @Json(name = "lng")
    var lng: Double? = 0.0
)