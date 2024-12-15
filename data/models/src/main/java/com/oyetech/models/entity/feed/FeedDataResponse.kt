package com.oyetech.models.entity.feed

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.general.IsOnlineBaseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-4.03.2022-
-00:11-
 **/

@Parcelize
@Keep
data class FeedDataResponse(
    var distanceInMeters: Double = 0.0,
    var isOnline: Boolean = false,
    var lastOnline: String = "",
    var lastOnlineText: String = "",
    var nick: String = "",
    var biography: String = "",
    var profilePhoto: String = "",
    var isPremiumMember: Boolean = false,
    var userId: Long = 0,
) : Parcelable, IsOnlineBaseData(_isOnline = isOnline, baseUserId = userId)

@Keep
@JsonClass(generateAdapter = true)
data class FeedDataSubResponse(
    @Json(name = "distanceInMeters") var distanceInMeters: Double = 0.0,
    @Json(name = "isOnline") var isOnline: Boolean = false,
    @Json(name = "isPremiumMember") var isPremiumMember: Boolean = false,
    @Json(name = "lastOnline") var lastOnline: String = "",
    @Json(name = "lastOnlineText") var lastOnlineText: String = "",
    @Json(name = "nick") var nick: String = "",
    @Json(name = "biography") var biography: String = "",
    @Json(name = "profilePhoto") var profilePhoto: String = "",
    @Json(name = "userId") var userId: Long = 0,
)

fun FeedDataSubResponse.mapToNormalize(): FeedDataResponse {
    var feedDataResponse = FeedDataResponse(
        distanceInMeters = this.distanceInMeters,
        isOnline = this.isOnline,
        lastOnline = this.lastOnline,
        lastOnlineText = this.lastOnlineText,
        nick = this.nick,
        biography = this.biography,
        profilePhoto = this.profilePhoto,
        userId = this.userId,
        isPremiumMember = this.isPremiumMember
    )
    return feedDataResponse
}


