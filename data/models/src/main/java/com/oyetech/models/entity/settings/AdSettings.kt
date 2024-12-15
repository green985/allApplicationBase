package com.oyetech.models.entity.settings

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AdSettings(
    @Json(name = "interstitialAdUnitId")
    var interstitialAdUnitId: String = "",
    @Json(name = "messageLimitRewardedAdId")
    var rewardedAdUnitId: String = "",
    @Json(name = "feedBottomBannerAdId")
    var feedBottomBannerAdId: String = "",
    @Json(name = "messageDetailBottomBannerAdId")
    var messageDetailBottomBannerAdId: String = "",
    @Json(name = "messagesBottomBannerAdId")
    var messagesBottomBannerAdId: String = "",
    @Json(name = "messageLimitRewardedHighFloorAdId")
    var messageLimitRewardedHighFloorAdId: String = "",
    @Json(name = "appodealApplicationKey")
    var appodealApplicationKey: String = ""
) : Parcelable
