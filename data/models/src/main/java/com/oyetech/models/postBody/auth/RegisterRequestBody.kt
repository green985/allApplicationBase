package com.oyetech.models.postBody.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-7.03.2022-
-02:11-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class RegisterRequestBody(
    // @Json(name = "age") var age: Int = 0,
    @Json(name = "birthDate") var birthDate: String = "",
    @Json(name = "apiSecret") var apiSecret: String = "",
    @Json(name = "buildNumber") var buildNumber: String = "",
    @Json(name = "clientSecret") var clientSecret: String = "",
    @Json(name = "clientUniqueId") var clientUniqueId: String = "",
    @Json(name = "deviceModel") var deviceModel: String = "",
    @Json(name = "gender") var gender: String = "",
    @Json(name = "nick") var nick: String = "",
    @Json(name = "osVersion") var osVersion: String = "",
    @Json(name = "platform") var platform: String = "",
    @Json(name = "version") var version: String = "",
    @Json(name = "country") var country: String = "",
    @Json(name = "language") var language: String = "",
) : Parcelable
