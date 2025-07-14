package com.oyetech.models.postBody.location

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class LocationRequestBody(
    @Json(name = "latitude") var latitude: Double = 0.0,
    @Json(name = "longitude") var longitude: Double = 0.0,

    @Transient
    @Ignore
    var locationException: Exception? = null
) : Parcelable {
    companion object
}

fun LocationRequestBody.isValidLocation(): Boolean {
    return !(this.latitude == 0.0 && this.longitude == 0.0)
}

fun LocationRequestBody.isError(): Boolean {
    return locationException == null
}

fun LocationRequestBody.getErrorMessage(): String {
    if (locationException == null) {
        return this.locationException?.message ?: ""
    }
    return ""
}

fun LocationRequestBody.createStringForHereApi(): String {
    return this.latitude.toString() + "," + this.longitude.toString()
}
