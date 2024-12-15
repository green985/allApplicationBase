package com.oyetech.models.postBody.world

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-12.09.2022-
-01:46-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TranslateOperationRequestBody(
    @Json(name = "code") var languageCode: String = "",
) : Parcelable
