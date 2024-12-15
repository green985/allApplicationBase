package com.oyetech.models.entity.bibleProperties

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleChapterPropertyResponseData(
    @Json(name = "bookRecordId")
    var bookRecordId: String = "",
    @Json(name = "chapterId")
    var chapterId: Int = 0,
    @Json(name = "chapterName")
    var chapterName: String = "",
    @Json(name = "chapterRecordId")
    var chapterRecordId: String = "",
    @Json(name = "order")
    var order: Int = 0,
    @Json(name = "verseCount")
    var verseCount: Int = 0,
    @Json(name = "isRead")
    var isRead: Boolean = false,
    @Json(name = "verses")
    var verses: List<BibleVersePropertyData> = listOf(),
    @Json(name = "audioInfo")
    var audioPropertyList: List<BibleAudioPropertyResponseData> = listOf(),
) : Parcelable