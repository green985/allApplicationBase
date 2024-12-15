package com.oyetech.models.postBody.messages

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-24.04.2022-
-19:45-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class DeleteMessageRequestBody(
    @Json(name = "messageIds")
    var messageIds: List<Long> = listOf(),
    @Transient
    @Ignore
    @IgnoredOnParcel
    var rowIds: List<Long> = listOf()
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class DeleteConversationRequestBody(
    @Json(name = "conversationIds")
    var conversationId: List<Long> = listOf()
) : Parcelable
