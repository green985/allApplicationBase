package com.oyetech.models.postBody.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class AuthRequestBody(
    @Json(name = "apiSecret") var apiSecret: String = "",
    @Json(name = "buildNumber") var buildNumber: String = "",
    @Json(name = "clientSecret") var clientSecret: String = "",
    @Json(name = "clientUniqueId") var clientUniqueId: String = "",
    @Json(name = "deviceModel") var deviceModel: String = "",
    @Json(name = "osVersion") var osVersion: String = "",
    @Json(name = "password") var password: String = "",
    @Json(name = "platform") var platform: String = "",
    @Json(name = "usernameOrEmail") var usernameOrEmail: String = "",
    @Json(name = "version") var version: String = "",
) : Parcelable
