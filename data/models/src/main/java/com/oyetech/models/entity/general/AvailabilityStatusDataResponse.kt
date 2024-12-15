package com.oyetech.models.entity.general

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AvailabilityStatusDataResponse(
    @Json(name = "isOnline") var isOnline: Boolean = false,
    @Json(name = "lastOnline") var lastOnline: String = "",
    @Json(name = "lastOnlineText") var lastOnlineText: String = "",
    @Json(name = "userId") var userId: Long = 0,
) : Parcelable
