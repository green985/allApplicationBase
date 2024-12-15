package com.oyetech.models.postBody.location

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.utils.const.HelperConstant
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-18.09.2023-
-21:48-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class LocationHereRequestBody(
    @Json(name = "apiKey") var apiKey: String,
    @Json(name = "at") var locationString: String,
    @Json(name = "q") var searchParams: String,
    @Json(name = "size") var size: Int,
) : Parcelable

fun createLocationHereRequestBody(
    apiKey: String,
    locationRequestBody: LocationRequestBody,
): LocationHereRequestBody {
    var locationRequestBody = LocationHereRequestBody(
        apiKey = apiKey,
        locationString = locationRequestBody.createStringForHereApi(),
        searchParams = "church",
        size = HelperConstant.HERE_PLACE_SIZE
    )
    return locationRequestBody
}
