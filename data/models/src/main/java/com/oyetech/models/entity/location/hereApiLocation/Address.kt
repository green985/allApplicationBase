package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "city")
    var city: String? = "",
    @Json(name = "countryCode")
    var countryCode: String? = "",
    @Json(name = "countryName")
    var countryName: String? = "",
    @Json(name = "county")
    var county: String? = "",
    @Json(name = "district")
    var district: String? = "",
    @Json(name = "label")
    var label: String? = "",
    @Json(name = "postalCode")
    var postalCode: String? = "",
    @Json(name = "street")
    var street: String? = ""
)