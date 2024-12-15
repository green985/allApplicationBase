package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.oyetech.models.entity.user.UserPropertyDataResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-19.07.2022-
-10:39-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class MessageDetailSubDataResponse(
    @PrimaryKey(autoGenerate = true) var rowId: Long = 0,
    @Json(name = "id") var messageId: Long = 0,
    @Json(name = "tempId") var tempId: String = "",
    @Json(name = "content") var content: String = "",
    @Json(name = "contentType") var contentType: String = "",
    @ColumnInfo(name = "conversationId")
    @Json(name = "conversationId")
    var conversationId: Long = 0,
    @Json(name = "date") var date: String = "",
    @Json(name = "deliverDate") var deliverDate: String = "",
    @Json(name = "deliveryUrl") var deliveryUrl: String = "",

    @Json(name = "fromUser")
    var userProperty: UserPropertyDataResponse = UserPropertyDataResponse(),

    @Json(name = "toUserId") var toUserId: Long = 0L,
    @Json(name = "fromUserId") var fromUserId: Long = userProperty.id,
    @Json(name = "fromUserNick") var fromUserNick: String = "",

    @Json(name = "isDelivered") var isDelivered: Boolean = false,
    @Json(name = "isSeen") var isSeen: Boolean = false,
    // @Json(name = "isRead") var isRead: Boolean = false,
    @Json(name = "ownMessage") var ownMessage: Boolean = false,
    @Json(name = "seenDate") var seenDate: String = "",
    @Json(name = "milliseconds") var milliseconds: Long? = null,

    @Json(name = "profileImageUrl") var profileImageUrl: String? = null,
) : Parcelable

fun MessageDetailSubDataResponse.mapToNormalize(): MessageDetailDataResponse {
    var messageDetailDataResponse = MessageDetailDataResponse(
        messageId = this.messageId,
        tempId = this.tempId,
        content = this.content,
        contentType = this.contentType,
        conversationId = this.conversationId,
        date = this.date,
        deliverDate = this.deliverDate,
        deliveryUrl = this.deliveryUrl,
        userProperty = UserPropertyDataResponse(
            id = this.userProperty.id,
            nick = this.userProperty.nick
        ),
        toUserId = this.toUserId,
        fromUserId = this.fromUserId,
        fromUserNick = this.fromUserNick,
        isDelivered = this.isDelivered,
        isSeen = this.isSeen,
        ownMessage = this.ownMessage,
        seenDate = this.seenDate,
        milliseconds = this.milliseconds,
        profileImageUrl = this.profileImageUrl
    )
    return messageDetailDataResponse
}
