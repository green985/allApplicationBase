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
    @param:Json(name = "currentLocation")
    var currentLocation: LocationDataResponse = LocationDataResponse(),
    @param:Json(name = "discoveryEnabled") var discoveryEnabled: Boolean = false,
    @param:Json(name = "lookingForAgeFrom") var lookingForAgeFrom: Int = 0,
    @param:Json(name = "lookingForAgeTo") var lookingForAgeTo: Int = 0,
    @param:Json(name = "lookingForGender") var lookingForGender: String = "",
    @param:Json(name = "maxDistanceInMeters") var maxDistanceInMeters: Int = 0,
    @param:Json(name = "preferredAgeFrom") var preferredAgeFrom: Int = 0,
    @param:Json(name = "preferredAgeTo") var preferredAgeTo: Int = 0,
    @param:Json(name = "preferredGender") var preferredGender: String = "",
    @param:Json(name = "lookingForCountry") var country: String = "",
    @param:Json(name = "selectedLocation")
    var selectedLocation: LocationDataResponse = LocationDataResponse(),
) : Parcelable
