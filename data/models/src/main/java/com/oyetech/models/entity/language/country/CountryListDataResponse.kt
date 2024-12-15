package com.oyetech.models.entity.language.country

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class CountryListDataResponse(
    @Json(name = "baseLanguage") var baseLanguage: String = "",
    @Json(name = "baseLanguageCode") var baseLanguageCode: String = "",
    @Json(name = "countries") var countries: List<CountryPropertyDataResponse> = listOf(),
) : Parcelable
