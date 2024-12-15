package com.oyetech.models.postBody.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationListResponseData(
    @Json(name = "content")
    var content: NotificationContentResponseData = NotificationContentResponseData(),
    @Json(name = "date")
    var date: String = "",
    @Json(name = "notificationId")
    var notificationId: Int = 0,
    @Json(name = "notificationType")
    var notificationType: String = ""
) : Parcelable
