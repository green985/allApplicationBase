package com.oyetech.models.postBody.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class GetNotificationsRequestBody(
    @Json(name = "lastNotificationId") var lastNotificationId: Int = 0,
) : Parcelable
