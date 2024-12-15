package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BiblePropertyResponseData(
    @Json(name = "abbreviation")
    var abbreviation: String = "",
    @Json(name = "abbreviationLocal")
    var abbreviationLocal: String = "",
    /*
    // TODO will be asked
    @Json(name = "audioBibles")
    var audioBibles: List<Any> = listOf(),

     */
    @Json(name = "countries")
    var countries: List<BibleCountryResponseData> = listOf(),
    @Json(name = "availableAudioAccents")
    var availableAudioAccents: List<BibleAudioAccentPropertyResponseData> = listOf(),
    @Json(name = "dblId")
    var dblId: String = "",
    @Json(name = "description")
    var description: String = "",
    @Json(name = "descriptionLocal")
    var descriptionLocal: String = "",
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "language")
    var language: BibleLanguageResponseData = BibleLanguageResponseData(),
    @Json(name = "name")
    var name: String = "",
    @Json(name = "nameLocal")
    var nameLocal: String = "",
    @Json(name = "recordId")
    var recordId: String = "",
    @Json(name = "isPopular")
    var isPopular: Boolean = false,
    @Json(name = "hasAudio")
    var hasAudio: Boolean = false,
    /*
    // TODO will be asked
    @Json(name = "relatedDbl")
    var relatedDbl: Any = Any(),

     */
    @Json(name = "type")
    var type: String = "",
    @Json(name = "updatedAt")
    var updatedAt: String = "",
) : Parcelable