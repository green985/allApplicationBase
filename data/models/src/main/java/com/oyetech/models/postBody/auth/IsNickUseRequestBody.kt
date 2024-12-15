package com.oyetech.models.postBody.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class IsNickUseRequestBody(
    @Json(name = "nick") var nick: String = "",
) : Parcelable
