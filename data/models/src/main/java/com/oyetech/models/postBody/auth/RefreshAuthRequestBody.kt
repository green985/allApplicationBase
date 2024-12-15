package com.oyetech.models.postBody.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class RefreshAuthRequestBody(
    @Json(name = "apiSecret") var apiSecret: String = "",
    @Json(name = "buildNumber") var buildNumber: String = "",
    @Json(name = "clientSecret") var clientSecret: String = "",
    @Json(name = "clientUniqueId") var clientUniqueId: String = "",
    @Json(name = "deviceModel") var deviceModel: String = "",
    @Json(name = "osVersion") var osVersion: String = "",
    @Json(name = "platform") var platform: String = "",
    @Json(name = "refreshToken") var refreshToken: String = "",
    @Json(name = "version") var version: String = "",
) : Parcelable
