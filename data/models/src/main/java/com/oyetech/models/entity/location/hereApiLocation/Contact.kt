package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Contact(
    @Json(name = "phone")
    var phone: List<Phone?>? = listOf(),
    @Json(name = "www")
    var www: List<Www?>? = listOf()
)