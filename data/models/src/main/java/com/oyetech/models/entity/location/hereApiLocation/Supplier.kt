package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Supplier(
    @Json(name = "id")
    var id: String? = ""
)