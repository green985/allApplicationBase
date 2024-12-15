package com.oyetech.models.entity.location

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class LocationDataResponse(
    @Json(name = "latitude") var latitude: Double = 0.0,
    @Json(name = "longitude") var longitude: Double = 0.0,
) : Parcelable
