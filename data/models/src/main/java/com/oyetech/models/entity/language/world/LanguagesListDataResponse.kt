package com.oyetech.models.entity.language.world

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class LanguagesListDataResponse(
    @Json(name = "baseLanguage") var baseLanguage: String = "",
    @Json(name = "baseLanguageCode") var baseLanguageCode: String = "",
    @Json(name = "languages") var languages: List<LanguagePropertyDataResponse> = listOf(),
) : Parcelable
