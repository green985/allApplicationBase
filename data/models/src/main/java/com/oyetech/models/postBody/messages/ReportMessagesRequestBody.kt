package com.oyetech.models.postBody.messages

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-28.04.2022-
-14:32-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ReportMessagesRequestBody(
    @Json(name = "messageIds")
    var messageIds: List<Long> = listOf(),
    @Json(name = "reportCategory")
    var reportCategory: Int = 0
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ReportConversationRequestBody(
    @Json(name = "conversationId")
    var conversationId: Long = 0L,
    @Json(name = "reportCategory")
    var reportCategory: Int = 0
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ReportUserRequestBody(
    @Json(name = "userId")
    var userId: Long = 0L,
    @Json(name = "reportCategory")
    var reportCategory: Int = 0
) : Parcelable
