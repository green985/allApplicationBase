package com.oyetech.models.entity.homePage

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class VerseOfTheDayResponseData(
    @Json(name = "chapter")
    var chapter: String? = "",
    @Json(name = "content")
    var content: String? = "",
) : Parcelable