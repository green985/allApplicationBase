package com.oyetech.models.entity.chat

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.location.LocationDataResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ChatPreferencesDataResponse(
    @Json(name = "currentLocation")
    var currentLocation: LocationDataResponse = LocationDataResponse(),
    @Json(name = "discoveryEnabled") var discoveryEnabled: Boolean = false,
    @Json(name = "lookingForAgeFrom") var lookingForAgeFrom: Int = 0,
    @Json(name = "lookingForAgeTo") var lookingForAgeTo: Int = 0,
    @Json(name = "lookingForGender") var lookingForGender: String = "",
    @Json(name = "maxDistanceInMeters") var maxDistanceInMeters: Int = 0,
    @Json(name = "preferredAgeFrom") var preferredAgeFrom: Int = 0,
    @Json(name = "preferredAgeTo") var preferredAgeTo: Int = 0,
    @Json(name = "preferredGender") var preferredGender: String = "",
    @Json(name = "lookingForCountry") var country: String = "",
    @Json(name = "selectedLocation")
    var selectedLocation: LocationDataResponse = LocationDataResponse(),
) : Parcelable
