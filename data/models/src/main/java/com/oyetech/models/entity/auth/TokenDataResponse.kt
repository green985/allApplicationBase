package com.oyetech.models.entity.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class TokenDataResponse(
    @Json(name = "expires") var expires: String = "",
    @Json(name = "issueAt") var issueAt: String = "",
    @Json(name = "token") var token: String = "",
) : Parcelable
