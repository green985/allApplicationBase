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
    @param:Json(name = "isFollowedByMe")
    var isFollowedByMe: Boolean = false,
    @param:Json(name = "isMe")
    var isMe: Boolean = false,
    @param:Json(name = "nick")
    var nick: String = "",
    @param:Json(name = "profilePhoto")
    var profilePhoto: String = "",
    @param:Json(name = "userId")
    var userId: Long = 0,
) : Parcelable
