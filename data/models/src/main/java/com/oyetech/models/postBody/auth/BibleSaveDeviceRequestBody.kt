package com.oyetech.models.postBody.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-17.11.2023-
-22:47-
 **/

/*
{
  "clientUniqueId": guid uret,
  "clientSecret": guid uret,
  "platform": "string",
  "version": "string",
  "buildNumber": 0,
  "notificationToken": "string",
  "osVersion": "string",
  "deviceModel": "string",
  "defaultLanguage": "string",
  "defaultBibleId": 0
}
 */

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleSaveDeviceRequestBody(
    @Json(name = "buildNumber") var buildNumber: String = "",
    @Json(name = "clientSecret") var clientSecret: String = "",
    @Json(name = "clientUniqueId") var clientUniqueId: String = "",
    @Json(name = "deviceModel") var deviceModel: String = "",
    @Json(name = "notificationToken") var notificationToken: String = "",
    @Json(name = "osVersion") var osVersion: String = "",
    @Json(name = "platform") var platform: String = "",
    @Json(name = "version") var version: String = "",
    @Json(name = "defaultBibleId") var defaultBibleId: Int = 0,
) : Parcelable
