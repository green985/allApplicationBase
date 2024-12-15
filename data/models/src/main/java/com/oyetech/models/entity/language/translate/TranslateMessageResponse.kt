package com.oyetech.models.entity.language.translate

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-10.08.2023-
-16:49-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TranslateMessageResponse(
    @Json(name = "translatedContent") var translatedContent: String = "",
    @Json(name = "translationLimitExceeded") var translationLimitExceeded: Boolean = false
) : Parcelable

