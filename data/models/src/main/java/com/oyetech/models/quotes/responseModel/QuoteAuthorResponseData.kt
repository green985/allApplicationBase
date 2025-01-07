package com.oyetech.models.quotes.responseModel

import androidx.annotation.Keep
import com.squareup.moshi.Json
import java.util.Date

/**
Created by Erdi Özbek
-7.01.2025-
-23:06-
 **/

@Keep
data class QuoteAuthorResponseData(
    @Json(name = "createdAt")
    var createdAt: Long = Date().time,

    @Json(name = "a")
    var authorDisplayName: String = "",
    @Json(name = "t")
    var authorId: String = "",
    @Json(name = "i")
    var authorImage: String = "",

    ) {
}