package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-1.10.2022-
-17:54-
 **/

@Keep
@Parcelize
data class UserAlertInfoResponseData(
    @Json(name = "alertCode")
    var alertCode: Int,
    @Json(name = "alertStringList")
    var alertStringList: List<UserAlertInfoAlertStringRequestBody>
) : Parcelable

@Keep
@Parcelize
data class UserAlertInfoStringResponseData(
    @Json(name = "alertCode")
    var alertCode: Int,
    @Json(name = "alertString")
    var alertString: String
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserAlertInfoAlertStringRequestBody(
    @Json(name = "alertString") var alertString: String = "",
    @Json(name = "languageCode") var languageCode: String = "",
) : Parcelable

