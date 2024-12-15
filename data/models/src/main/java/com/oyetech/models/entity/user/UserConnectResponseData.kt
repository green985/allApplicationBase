package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserConnectResponseData(
    @Json(name = "isFollowedByMe")
    var isFollowedByMe: Boolean = false,
    @Json(name = "isMe")
    var isMe: Boolean = false,
    @Json(name = "nick")
    var nick: String = "",
    @Json(name = "profilePhoto")
    var profilePhoto: String = "",
    @Json(name = "userId")
    var userId: Long = 0
) : Parcelable
