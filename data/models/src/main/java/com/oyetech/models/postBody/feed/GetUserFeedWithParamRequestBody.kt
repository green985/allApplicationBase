package com.oyetech.models.postBody.feed

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.postBody.location.LocationRequestBody
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class GetUserFeedWithParamRequestBody(
    @Json(name = "currentLocation")
    var currentLocation: LocationRequestBody = LocationRequestBody(),
    @Json(name = "lookingForAgeFrom") var lookingForAgeFrom: Int = 0,
    @Json(name = "lookingForAgeTo") var lookingForAgeTo: Int = 0,
    @Json(name = "lookingForGender") var lookingForGender: String = "",
    @Json(name = "maxDistanceInMeters") var maxDistanceInMeters: Int = 0,
    @Json(name = "preferredAgeFrom") var preferredAgeFrom: Int = 0,
    @Json(name = "preferredAgeTo") var preferredAgeTo: Int = 0,
    @Json(name = "preferredGender") var preferredGender: String = "",
    @Json(name = "selectedLocation")
    var selectedLocation: LocationRequestBody = LocationRequestBody(),
) : Parcelable
