package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oyetech.models.utils.states.MessagesState
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-28.10.2022-
-01:04-
 **/

@Entity
@Parcelize
data class MessagesConversationStatusData(
    @PrimaryKey
    @ColumnInfo(name = "conversationId")
    @Json(name = "conversationId")
    var conversationId: Long,

    @Json(name = "status")
    @ColumnInfo(name = "status")
    var messageStatus: @MessagesState Int
) : Parcelable
