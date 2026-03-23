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
    @param:Json(name = "expires") var expires: String = "",
    @param:Json(name = "issueAt") var issueAt: String = "",
    @param:Json(name = "token") var token: String = "",
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
data class TokenDataResponse22(
    @param:Json(name = "expires") var expires: String = "",
    @param:Json(name = "issueAt") var issueAt: String = "",
    @param:Json(name = "token") var token: String = "",
)
