package com.oyetech.models.entity

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
Created by Erdi Özbek
-1.03.2022-
-20:25-
 **/

@Keep
@JsonClass(generateAdapter = true)
class GenericResponse<T> {
    @Json(name = "data")
    var resultObject: T? = null

    @Json(name = "message")
    var resultMessage: String = ""

    @Json(name = "status")
    var resultStatus: Boolean = false
}
