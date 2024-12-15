package com.oyetech.models.postBody.settings

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class SettingsInfoRequestBody(
    @Json(name = "apiSecret")
    var apiSecret: String = "",
    @Json(name = "buildNumber")
    var buildNumber: String = "",
    @Json(name = "clientSecret")
    var clientSecret: String = "",
    @Json(name = "platform")
    var platform: String = ""
) : Parcelable
