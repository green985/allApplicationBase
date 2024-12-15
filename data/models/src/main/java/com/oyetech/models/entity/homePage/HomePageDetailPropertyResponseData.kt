package com.oyetech.models.entity.homePage

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleVersePropertyData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class HomePageDetailPropertyResponseData(
    @Json(name = "lastReadBibleId")
    var lastReadBibleId: Int? = 0,
    @Json(name = "lastReadPercentage")
    var lastReadPercentage: Double? = 0.0,
    @Json(name = "prayerOfTheDay")
    var prayerOfTheDay: String? = "",
    @Json(name = "verseOfTheDay")
    var verseOfTheDay: BibleVersePropertyData? = BibleVersePropertyData(),

    var biblePropertyResponseData: BiblePropertyResponseData = BiblePropertyResponseData(),

    ) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class HomePagePropertyResponseData(
    @Json(name = "lastReadBibleId")
    var lastReadBibleId: Int? = 0,
    @Json(name = "lastReadPercentage")
    var lastReadPercentage: Double? = 0.0,
    @Json(name = "verses")
    var verses: List<BibleVersePropertyData>? = emptyList(),
    @Json(name = "prays")
    var prays: List<String>? = emptyList(),

    ) : Parcelable

fun HomePagePropertyResponseData.mapToHomePageDetailProperty(
    biblePropertyResponseData: BiblePropertyResponseData?,
    prayerOfTheDay: String?,
    verseOfTheDay: BibleVersePropertyData?,
): HomePageDetailPropertyResponseData {
    this.let {
        var data = HomePageDetailPropertyResponseData(
            lastReadBibleId = it.lastReadBibleId,
            lastReadPercentage = it.lastReadPercentage,
            prayerOfTheDay = prayerOfTheDay,
            verseOfTheDay = verseOfTheDay,
            biblePropertyResponseData = biblePropertyResponseData ?: BiblePropertyResponseData()
        )
        return data
    }
}