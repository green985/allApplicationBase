package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oyetech.models.entity.contentProperties.AudioDurationModel
import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.audio.AudioMessageResponseSubData
import com.oyetech.models.entity.file.audio.mapToNormalize
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseSubData
import com.oyetech.models.entity.file.imageFile.mapToNormalize
import com.oyetech.models.entity.user.UserPropertyDataResponse
import com.oyetech.models.entity.user.UserTypingSendingOperationStatusData
import com.oyetech.models.postBody.messages.MessageRequestBody
import com.oyetech.models.sealeds.messagess.MessageTypeUtil
import com.oyetech.models.utils.helper.TimeFunctions.getDateFromLongWithoutHour
import com.oyetech.models.utils.moshi.deserialize
import com.oyetech.models.utils.states.SENDING
import com.oyetech.models.utils.states.SocketUserOperation
import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "messageDetailData", indices = [Index(value = ["conversationId"])],
    foreignKeys = [
        ForeignKey(
            entity = MessageConversationDataResponse::class,
            parentColumns = arrayOf("conversationId"),
            childColumns = arrayOf("conversationId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
@Keep
data class MessageDetailDataResponse(
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
    var status: Int = SENDING,

    var audioDuration: Long? = null,

    var translatedString: String? = null,

    var modifiedAt: Long = milliseconds ?: System.currentTimeMillis(),
    var dateDayString: String = getDateFromLongWithoutHour(modifiedAt),

    @Ignore var listGroupDateTag: String? = null,

    @Ignore
    @Transient
    var toUserName: String? = null,

    @Ignore
    @Json(name = "profileImageUrl")
    var profileImageUrl: String? = null,
) : Parcelable {
    // constructor() : this(0)
    @Ignore
    @IgnoredOnParcel
    @Transient
    var audioDurationModel: AudioDurationModel = AudioDurationModel()

    @Ignore
    @IgnoredOnParcel
    @Transient
    var imagePropertyData: ImageMessageResponseData? = null
        get() {
            if (contentType != MessageTypeUtil.Image()) {
                return null
            } else {
                return try {
                    content.deserialize<ImageMessageResponseSubData>()?.mapToNormalize()
                } catch (e: Exception) {
                    return field
                }
            }
        }

    @Ignore
    @IgnoredOnParcel
    @Transient
    var audioPropertyData: AudioMessageResponseData? = null
        get() {
            if (contentType != MessageTypeUtil.Audio()) {
                return null
            } else {
                return try {
                    content.deserialize<AudioMessageResponseSubData>()?.mapToNormalize()
                } catch (e: Exception) {
                    return field
                }
            }
        }
}

fun MessageDetailDataResponse.mapToMessageRequestBody(): MessageRequestBody {
    var messageRequestBody =
        MessageRequestBody(
            tempId = this.tempId,
            id = 0,
            conversationId = this.conversationId,
            toUserId = this.fromUserId,
            content = this.content,
            contentType = this.contentType,
            date = this.date,
        )
    return messageRequestBody
}

fun MessageDetailDataResponse.mapToMessageConversationRequestBody(): MessageConversationDataResponse {
    var messageConversationDataResponse =
        MessageConversationDataResponse(
            conversationId = this.conversationId,
            content = this.content,
            contentType = this.contentType,
            date = this.date,
            userId = this.fromUserId,
            fromUserId = this.fromUserId,
            nick = this.fromUserNick,
            profilePhoto = this.profileImageUrl ?: ""
        )
    return messageConversationDataResponse
}

fun List<MessageDetailDataResponse>.containsWithId(messageDetailDataResponse: MessageDetailDataResponse?): Boolean {
    var foundedItem = this.find {
        it.messageId == messageDetailDataResponse?.messageId
    }
    return foundedItem != null
}

fun List<MessageDetailDataResponse>.containsWithTempId(messageDetailDataResponse: MessageDetailDataResponse?): Boolean {
    var foundedItem = this.find {
        it.tempId == messageDetailDataResponse?.tempId
    }
    return foundedItem != null
}

fun MessageDetailDataResponse.generateUserOperationBody(operationType: SocketUserOperation): UserTypingSendingOperationStatusData {
    var data = UserTypingSendingOperationStatusData(this.fromUserId, operationType)
    return data
}

fun MessageDetailDataResponse?.canShowPopupMenuForMessage(): Boolean {
    if (this == null) return false

    if (this.contentType == MessageTypeUtil.Text()) {
        return true
    }
    return false
}


