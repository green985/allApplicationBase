package com.oyetech.models.entity.messages

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-13.10.2022-
-16:04-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class MessagesLimitInfoResponseData(
    @Json(name = "TempId") var tempId: String = "",
    @Json(name = "UnlockTime") var unlockTime: String = "",
    @Json(name = "AllowShowingRewardedAds") var allowShowingRewardedAds: Boolean = true,
    @Json(name = "AllowSubscription") var allowSubscription: Boolean = true,
) : Parcelable
