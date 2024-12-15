package com.oyetech.models.postBody.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationContentResponseData(
    @Json(name = "message")
    var message: String = "",
    @Json(name = "nick")
    var nick: String = "",
    @Json(name = "profileImageUrl")
    var profileImageUrl: String = "",
    @Json(name = "userId")
    var userId: Int = 0
) : Parcelable

