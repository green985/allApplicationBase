package com.oyetech.models.entity.language

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TextResourcesDataResponse(
    @Json(name = "directionality") var directionality: String = "",
    @Json(name = "languageCode") var languageCode: String = "",
    @Json(name = "languageLocalName") var languageLocalName: String = "",
    @Json(name = "languageName") var languageName: String = "",
    @Json(name = "translations") var translations: List<TranslationDataResponse> = listOf(),
) : Parcelable
