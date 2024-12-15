package com.oyetech.models.entity.language

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TranslationDataResponse(
    @Json(name = "key") var key: String = "",
    @Json(name = "translationValue") var translationValue: String = "",
) : Parcelable
