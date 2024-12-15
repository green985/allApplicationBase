package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class HereLocationDataResponse(
    @Json(name = "items")
    var items: List<LocationItem?>? = listOf()
)