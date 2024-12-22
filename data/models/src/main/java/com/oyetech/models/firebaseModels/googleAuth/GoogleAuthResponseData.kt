package com.oyetech.models.firebaseModels.googleAuth

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-17.06.2024-
-00:02-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class GoogleAuthResponseData(
    val id: String? = "",
    val email: String? = "",
    val name: String? = "",
    val photoUrl: String? = "",
    val token: String? = "",
    val refreshToken: String? = "",
    val expiresIn: Long = 0,
    val timeStamp: Long = 0,
    val idToken: String? = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorException: Exception? = null,

    ) : Parcelable

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class GoogleAnonymousAuthResponseData(
    val id: String? = "",
    val token: String? = "",
    val refreshToken: String? = "",
    val expiresIn: Long = 0,
    val timeStamp: Long = 0,
    val idToken: String? = "",
    val errorMessage: String? = null,
    val errorException: Exception? = null,
) : Parcelable