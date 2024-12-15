package com.oyetech.models.entity.user

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class UserPersonalInfoDataResponse(
    @Json(name = "birthDate")
    var birthDate: String = "",
    @Json(name = "country")
    var country: String = "",
    @Json(name = "nick")
    var nick: String = ""
) : Parcelable
