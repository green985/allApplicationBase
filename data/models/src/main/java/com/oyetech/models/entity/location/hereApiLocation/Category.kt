package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id")
    var id: String? = "",
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "primary")
    var primary: Boolean? = false
)