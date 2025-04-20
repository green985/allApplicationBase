package com.oyetech.models.firebaseModels.messagingModels

import android.icu.util.Calendar
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.firebase.firestore.ServerTimestamp
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus.IDLE
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
Created by Erdi Özbek
-16.02.2025-
-17:00-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseMessageConversationData(
    var conversationId: String = "",
    var participantList: List<FirebaseParticipantData> = emptyList(),
    var lastMessageId: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
    var participantUserIdList: List<String> = participantList.map { it.userId }.sorted(),

    @Ignore
    var lastMessage: FirebaseMessagingLocalData? = null,

    ) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
data class FirebaseMessagingResponseData(
    @PrimaryKey
    var messageId: String = "",
    var conversationId: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var messageText: String = "",
    var status: MessageStatus = MessageStatus.IDLE,
    @ServerTimestamp
    var createdAt: Date? = null,

    @Ignore
    var timestamp: Any? = null,
)

@Parcelize
@Keep
@Entity(tableName = "messages")
@JsonClass(generateAdapter = true)
data class FirebaseMessagingLocalData(
    @PrimaryKey
    var messageId: String = "",
    var conversationId: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var messageText: String = "",
    var status: MessageStatus = MessageStatus.IDLE,
    var createdAt: Long? = System.currentTimeMillis(),

//    var mediaType: String? = null,
//    var mediaUrl: String? = null,
) : Parcelable

@Parcelize
@Keep
data class FirebaseNotificationPostData(
    @PrimaryKey
    var type: String = "",
    var dataString: String = "",
) : Parcelable

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseParticipantData(
    var userId: String = "",
    var username: String = "",
    var isActiveUser: Boolean = false,
) : Parcelable

fun FirebaseMessagingResponseData.toLocalDataWithStatus(status: MessageStatus = IDLE): FirebaseMessagingLocalData {
    return FirebaseMessagingLocalData(
        messageId = messageId,
        conversationId = conversationId,
        senderId = senderId,
        receiverId = receiverId,
        messageText = messageText,
        status = status,
        createdAt = createdAt?.time ?: 0L,
    )
}

fun FirebaseMessagingResponseData.toLocalData(): FirebaseMessagingLocalData {
    return FirebaseMessagingLocalData(
        messageId = messageId,
        conversationId = conversationId,
        senderId = senderId,
        receiverId = receiverId,
        messageText = messageText,
        status = status,
        createdAt = createdAt?.time ?: Calendar.getInstance().timeInMillis,
    )
}

// local to remote
fun FirebaseMessagingLocalData.toRemoteData(): FirebaseMessagingResponseData {
    return FirebaseMessagingResponseData(
        messageId = messageId,
        conversationId = conversationId,
        senderId = senderId,
        receiverId = receiverId,
        messageText = messageText,
        status = status,
        createdAt = createdAt?.let { Date(it) },
    )
}

enum class MessageStatus {
    IDLE,
    SENT,
    ERROR,
    DELIVERED,
    READ
}

class MessageStatusTypeConverter {

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus {
        return MessageStatus.valueOf(status)
    }

}