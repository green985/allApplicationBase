package com.oyetech.models.quotes.responseModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-16.12.2024-
-22:18-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class QuoteResponseData(
    @Json(name = "q")
    var text: String = "",
    @Json(name = "a")
    var author: String = "",
    @Json(name = "c")
    var charCount: Int = 0,
    @Json(name = "i")
    var authorImage: String = "",
    @Json(name = "htmlFormatted")
    var htmlFormatted: String = "",
) : Parcelable