package com.oyetech.models.radioProject.entity.radioEntity.country

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class CountryResponseData(
    @Json(name = "name")
    var name: String = "",
    @Json(name = "countryFullName")
    var countryFullName: String = "",
    @Json(name = "stationcount")
    var stationcount: Int = 0,
    @Transient
    var flagId: Int = 0,
) : Parcelable