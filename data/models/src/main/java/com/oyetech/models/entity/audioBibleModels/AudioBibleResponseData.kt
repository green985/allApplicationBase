package com.oyetech.models.entity.audioBibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.bibleModels.BibleCountryResponseData
import com.oyetech.models.entity.bibleModels.BibleLanguageResponseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioBibleResponseData(
    @Json(name = "abbreviation")
    var abbreviation: String? = "",
    @Json(name = "abbreviationLocal")
    var abbreviationLocal: String? = "",
    @Json(name = "copyright")
    var copyright: String? = "",
    @Json(name = "countries")
    var countries: List<BibleCountryResponseData?>? = listOf(),
    @Json(name = "dblId")
    var dblId: String? = "",
    @Json(name = "description")
    var description: String? = "",
    @Json(name = "descriptionLocal")
    var descriptionLocal: String? = "",
    @Json(name = "id")
    var id: String? = "",
    @Json(name = "info")
    var info: String? = "",
    @Json(name = "language")
    var language: BibleLanguageResponseData? = BibleLanguageResponseData(),
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "nameLocal")
    var nameLocal: String? = "",
    @Json(name = "relatedDbl")
    var relatedDbl: String? = "",
    @Json(name = "type")
    var type: String? = "",
    @Json(name = "updatedAt")
    var updatedAt: String? = "",
) : Parcelable