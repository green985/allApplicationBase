package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-2.04.2022-
-14:16-
 **/
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class MessagesStatusOperationSocketResponse(
    @Json(name = "conversationId") var conversationId: Long = 0,
    @Json(name = "id") var messageId: Long = 0,
) : Parcelable
