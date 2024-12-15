package com.oyetech.models.entity.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class AuthRequestResponse(
    @Json(name = "accessToken") var accessToken: TokenDataResponse = TokenDataResponse(),
    @Json(name = "message") var message: String = "",
    @Json(name = "nick") var nick: String = "",
    @Json(name = "language") var languageCode: String = "",
    @Json(name = "refreshToken") var refreshToken: TokenDataResponse = TokenDataResponse(),
    @Json(name = "userId") var userId: Long = 0,
) : Parcelable {

    fun isNeedRegisterComplete(): Boolean {
        return message == UserStatusKey.ACCOUNT_CREATION_REQUIRED
    }

    fun isGenderSelectNeeded(): Boolean {
        return message == UserStatusKey.ACCOUNT_GENDER_CREATION_REQUIRED
    }

    fun isAccountBanned(): Boolean {
        return message == UserStatusKey.ACCOUNT_BANNED
    }

    fun isSuccess(): Boolean {
        return message == UserStatusKey.SUCCESS
    }
}
