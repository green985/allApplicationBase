package com.oyetech.models.firebaseModels.messagingModels

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
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
    val conversationId: String = "",
    val participantList: List<FirebaseParticipantData> = emptyList(),
    val lastMessageId: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val participantUserIdList: List<String> = participantList.map { it.userId }.sorted(),

    ) : Parcelable

@Parcelize
@Entity(tableName = "messages")
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseMessagingResponseData(
    @PrimaryKey
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val status: MessageStatus = MessageStatus.IDLE,
    @ServerTimestamp
    val createdAt: Date? = null,
//    val mediaType: String? = null,
//    val mediaUrl: String? = null,
) : Parcelable

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseParticipantData(
    val userId: String = "",
    val username: String = "",
    val isActiveUser: Boolean = false,
) : Parcelable

enum class MessageStatus {
    IDLE,
    SENT,
    DELIVERED,
    READ
}