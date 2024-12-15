package com.oyetech.models.postBody.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserOperationRequestBody(
    @Json(name = "userId") var userId: Long = 0,
) : Parcelable
