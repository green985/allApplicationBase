package com.oyetech.models.postBody.messages

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class MessagesBeforeAfterRequestBody(
    @Json(name = "conversationId") var conversationId: Long = 0,
    @Json(name = "messageId") var messageId: Long = 0,

    @Transient
    @Ignore var userId: Long = 0L,
    @Transient
    @Ignore var toUserNickname: String = "",
    @Transient
    @Ignore var profileImageUrl: String = "",
) : Parcelable {

    fun isEmpty(): Boolean {
        if (messageId == 0L && conversationId == 0L) {
            return true
        }
        return false
    }
}
