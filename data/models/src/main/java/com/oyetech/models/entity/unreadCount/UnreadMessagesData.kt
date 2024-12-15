package com.oyetech.models.entity.unreadCount

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-23.04.2022-
-01:01-
 **/

@Entity
@Parcelize
data class UnreadMessagesData(
    @PrimaryKey
    @ColumnInfo(name = "conversationId")
    @Json(name = "conversationId")
    var conversationId: Long,

    @Json(name = "unreadMessageCount")
    @ColumnInfo(name = "unreadMessageCount")
    var unreadMessageCount: Int
) : Parcelable
