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
    @param:Json(name = "accessToken") var accessToken: TokenDataResponse = TokenDataResponse(),
    @param:Json(name = "message") var message: String = "",
    @param:Json(name = "nick") var nick: String = "",
    @param:Json(name = "language") var languageCode: String = "",
    @param:Json(name = "refreshToken") var refreshToken: TokenDataResponse = TokenDataResponse(),
    @param:Json(name = "userId") var userId: Long = 0,
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
