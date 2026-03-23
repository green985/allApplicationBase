package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class UserPropertyDataResponse(
    @param:Json(name = "id") var id: Long = 0,
    @param:Json(name = "nick") var nick: String = "",
) : Parcelable
