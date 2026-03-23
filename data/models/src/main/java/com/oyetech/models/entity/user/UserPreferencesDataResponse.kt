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
    @param:Json(name = "allowMessagesFrom") var allowMessagesFrom: String = "",
    @param:Json(name = "hideAvailability") var hideAvailability: Boolean = false,
    @param:Json(name = "hideFollowers") var hideFollowers: Boolean = false,
    @param:Json(name = "hideFollowings") var hideFollowings: Boolean = false,
    @param:Json(name = "hideMessageSeenStatus") var hideMessageSeenStatus: Boolean = false,
) : Parcelable
