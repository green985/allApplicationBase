package com.oyetech.models.entity.language.world

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class LanguagePropertyDataResponse(
    @Json(name = "code") var code: String = "",
    @Json(name = "directionality") var directionality: String = "",
    @Json(name = "englishName") var englishName: String = "",
    @Json(name = "localName") var localName: String = "",
    @Json(name = "name") var name: String = "",
    @Json(name = "translatedName") var translatedName: String = "",
    @Transient
    var isSelected: Boolean = false
) : Parcelable
