package com.oyetech.models.postBody.world

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class LanguageCodeRequestBody(
    @Json(name = "languageCode") var languageCode: String = "",
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserLanguageRequestBody(
    @Json(name = "code") var languageCode: String = "",
) : Parcelable
