package com.oyetech.models.entity.language.country

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class CountryPropertyDataResponse(
    @Json(name = "id") var id: Int = 0,
    @Json(name = "code") var code: String = "",
    @Json(name = "name") var name: String = "",
    @Json(name = "translatedName") var translatedName: String = "",
    @Json(name = "flag") var flag: String = "",
) : Parcelable
