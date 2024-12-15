package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserPreferencesDataResponse(
    @Json(name = "allowMessagesFrom") var allowMessagesFrom: String = "",
    @Json(name = "hideAvailability") var hideAvailability: Boolean = false,
    @Json(name = "hideFollowers") var hideFollowers: Boolean = false,
    @Json(name = "hideFollowings") var hideFollowings: Boolean = false,
    @Json(name = "hideMessageSeenStatus") var hideMessageSeenStatus: Boolean = false,
) : Parcelable
