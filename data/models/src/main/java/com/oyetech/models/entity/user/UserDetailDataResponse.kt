package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.general.AvailabilityStatusDataResponse
import com.oyetech.models.entity.general.IsOnlineBaseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserDetailDataResponse(
    @Json(name = "biography")
    var biography: String = "",
    @Json(name = "blockedProfile")
    var blockedProfile: Boolean = false,
    @Json(name = "followInfo")
    var followInfo: String = "",
    @Json(name = "followerCount")
    var followerCount: Int = 0,
    @Json(name = "following")
    var following: Boolean = false,
    @Json(name = "likedProfile")
    var likedProfile: Boolean = false,
    @Json(name = "followingCount")
    var followingCount: Int = 0,
    @Json(name = "followsYou")
    var followsYou: Boolean = false,
    @Json(name = "likeCount")
    var likeCount: Int = 0,
    @Json(name = "messagingAllowed")
    var messagingAllowed: Boolean = false,
    @Json(name = "nick")
    var nick: String = "",
    @Json(name = "ownProfile")
    var ownProfile: Boolean = false,
    @Json(name = "isPremiumMember")
    var isPremiumMember: Boolean = false,

    /*
    @Json(name = "posts")
    var posts: Any = Any(),
*/
    @Json(name = "availabilityStatus")
    var availabilityStatus: AvailabilityStatusDataResponse = AvailabilityStatusDataResponse(),

    @Json(name = "profileImages")
    var profileImages: List<UserProfileImageResponseData> = listOf(),
    @Json(name = "showAvailability")
    var showAvailability: Boolean = false,
    @Json(name = "userId")
    var userId: Long = 0
) : Parcelable, IsOnlineBaseData(
    baseUserId = availabilityStatus.userId,
    _isOnline = availabilityStatus.isOnline
)

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserDetailSubDataResponse(
    @Json(name = "biography")
    var biography: String = "",
    @Json(name = "blockedProfile")
    var blockedProfile: Boolean = false,
    @Json(name = "followInfo")
    var followInfo: String = "",
    @Json(name = "followerCount")
    var followerCount: Int = 0,
    @Json(name = "following")
    var following: Boolean = false,
    @Json(name = "likedProfile")
    var likedProfile: Boolean = false,
    @Json(name = "followingCount")
    var followingCount: Int = 0,
    @Json(name = "followsYou")
    var followsYou: Boolean = false,
    @Json(name = "likeCount")
    var likeCount: Int = 0,
    @Json(name = "messagingAllowed")
    var messagingAllowed: Boolean = false,
    @Json(name = "nick")
    var nick: String = "",
    @Json(name = "ownProfile")
    var ownProfile: Boolean = false,
    @Json(name = "isPremiumMember")
    var isPremiumMember: Boolean = false,

    /*
    @Json(name = "posts")
    var posts: Any = Any(),
*/
    @Json(name = "availabilityStatus")
    var availabilityStatus: AvailabilityStatusDataResponse = AvailabilityStatusDataResponse(),

    @Json(name = "profileImages")
    var profileImages: List<UserProfileImageResponseData> = listOf(),
    @Json(name = "showAvailability")
    var showAvailability: Boolean = false,
    @Json(name = "userId")
    var userId: Long = 0
) : Parcelable

fun UserDetailSubDataResponse.mapToNormalize(): UserDetailDataResponse {
    var userDetailDataResponse = UserDetailDataResponse(
        biography = this.biography,
        blockedProfile = this.blockedProfile,
        followInfo = this.followInfo,
        followerCount = this.followerCount,
        following = this.following,
        likedProfile = this.likedProfile,
        followingCount = this.followingCount,
        followsYou = this.followsYou,
        likeCount = this.likeCount,
        messagingAllowed = this.messagingAllowed,
        nick = this.nick,
        ownProfile = this.ownProfile,
        isPremiumMember = this.isPremiumMember,
        availabilityStatus = AvailabilityStatusDataResponse(
            isOnline = this.availabilityStatus.isOnline,
            lastOnline = this.availabilityStatus.lastOnline,
            lastOnlineText = this.availabilityStatus.lastOnlineText,
            userId = this.availabilityStatus.userId
        ),
        profileImages = this.profileImages,
        showAvailability = this.showAvailability,
        userId = this.userId
    )
    return userDetailDataResponse
}

