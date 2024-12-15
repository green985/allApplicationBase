package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.oyetech.models.BR
import com.oyetech.models.entity.feed.FeedDataResponse
import com.oyetech.models.entity.general.IsOnlineBaseData
import com.oyetech.models.entity.user.UserDetailDataResponse
import com.oyetech.models.utils.states.MessagesState
import com.oyetech.models.utils.states.SENDING
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "messageConversationData"
)
@Parcelize
@Keep
data class MessageConversationDataResponse(
    // @PrimaryKey(autoGenerate = true)
    var rowId: Long = 0,
    @PrimaryKey
    @Json(name = "conversationId") var conversationId: Long = 0,

    @Json(name = "content") var content: String = "",
    @Json(name = "contentType") var contentType: String = "",
    @Json(name = "conversationAccepted") var conversationAccepted: Boolean = false,
    @Json(name = "date") var date: String = "",
    @Json(name = "fromUserId") var fromUserId: Long = 0,
    @Json(name = "isOnline") var isOnline: Boolean = false,
    @Json(name = "lastOnline") var lastOnline: String = "",
    @Json(name = "lastOnlineText") var lastOnlineText: String = "",
    @Json(name = "maxMessageId") var maxMessageId: Long = 0,
    @Json(name = "nick") var nick: String = "",
    @Json(name = "profilePhoto") var profilePhoto: String = "",
    @Json(name = "unreadCount") var unreadCount: Int = 0,
    @Json(name = "userId") var userId: Long = 0,
    @Json(name = "isPremiumMember") var isPremiumMember: Boolean = false,

    @Json(name = "milliseconds") var milliseconds: Long? = null,
    var modifiedAt: Long = milliseconds ?: System.currentTimeMillis(),
    // var status: Int = SENDING,

    @Transient
    @Ignore
    var linkedMessageDetailDataResponse: MessageDetailDataResponse? = null,

    @Transient
    @Ignore
    var _unreadMessageCount: Int = 0,

    @Transient
    @Ignore
    var _lastMessageStatus: @MessagesState Int = SENDING,

    ) : Parcelable, IsOnlineBaseData(_isOnline = isOnline, baseUserId = userId) {

    @Json(ignore = true)
    var unreadMessageCount: Int
        @Bindable get() = _unreadMessageCount
        set(value) {
            _unreadMessageCount = value

            notifyPropertyChanged(BR.unreadMessageCount)
        }

    @Json(ignore = true)
    var lastMessageStatus: Int
        @Bindable get() = _lastMessageStatus
        set(value) {
            _lastMessageStatus = value

            notifyPropertyChanged(BR.lastMessageStatus)
        }
}

@Keep
@Parcelize
@JsonClass(generateAdapter = true)
data class MessageConversationSubDataResponse(
    @Json(name = "conversationId") var conversationId: Long = 0,
    @Json(name = "content") var content: String = "",
    @Json(name = "contentType") var contentType: String = "",
    @Json(name = "conversationAccepted") var conversationAccepted: Boolean = false,
    @Json(name = "date") var date: String = "",
    @Json(name = "fromUserId") var fromUserId: Long = 0,
    @Json(name = "isOnline") var isOnline: Boolean = false,
    @Json(name = "lastOnline") var lastOnline: String = "",
    @Json(name = "lastOnlineText") var lastOnlineText: String = "",
    @Json(name = "maxMessageId") var maxMessageId: Long = 0,
    @Json(name = "nick") var nick: String = "",
    @Json(name = "profilePhoto") var profilePhoto: String = "",
    @Json(name = "unreadCount") var unreadCount: Int = 0,
    @Json(name = "userId") var userId: Long = 0,
    @Json(name = "milliseconds") var milliseconds: Long? = null,
    @Json(name = "isPremiumMember") var isPremiumMember: Boolean = false,

    ) : Parcelable

fun MessageConversationSubDataResponse.mapToNormalize(): MessageConversationDataResponse {
    var messageConversationDataResponse = MessageConversationDataResponse(
        rowId = 0,
        conversationId = this.conversationId,
        content = this.content,
        contentType = this.contentType,
        conversationAccepted = this.conversationAccepted,
        date = this.date,
        fromUserId = this.fromUserId,
        isOnline = this.isOnline,
        lastOnline = this.lastOnline,
        lastOnlineText = this.lastOnlineText,
        maxMessageId = this.maxMessageId,
        nick = this.nick,
        profilePhoto = this.profilePhoto,
        unreadCount = this.unreadCount,
        userId = this.userId,
        milliseconds = this.milliseconds,
        isPremiumMember = this.isPremiumMember
    )
    return messageConversationDataResponse
}

fun List<MessageConversationDataResponse>.containsWithId(messageConversationDataResponse: MessageConversationDataResponse?): Boolean {
    var foundedItem = this.find {
        it.conversationId == messageConversationDataResponse?.conversationId
    }
    return foundedItem != null
}

fun UserDetailDataResponse.mapFromUserDataToMessageConversation(userData: UserDetailDataResponse): MessageConversationDataResponse {
    var profileUrl = ""
    if (!userData.profileImages.isNullOrEmpty()) {
        profileUrl = userData?.profileImages?.get(0)?.largeImageUrl ?: ""
    }
    var messageConversationDataResponse =
        MessageConversationDataResponse(
            userId = userData?.userId ?: 0L,
            nick = userData?.nick ?: "",
            profilePhoto = profileUrl,
            isOnline = userData.isOnlineView,
            isPremiumMember = userData.isPremiumMember
        )

    return messageConversationDataResponse
}

fun FeedDataResponse.feedDataToMessageConversationMapper(): MessageConversationDataResponse {
    var itemData = this
    var messageConversationDataResponse =
        MessageConversationDataResponse(
            userId = itemData?.userId ?: 0L,
            nick = itemData?.nick ?: "",
            profilePhoto = itemData?.profilePhoto ?: "",
            isOnline = itemData?.isOnline ?: false,
            isPremiumMember = itemData?.isPremiumMember ?: false
        )
    return messageConversationDataResponse
}


