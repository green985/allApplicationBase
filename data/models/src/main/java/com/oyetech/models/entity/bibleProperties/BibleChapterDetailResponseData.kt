package com.oyetech.models.entity.bibleProperties

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleChapterDetailResponseData(
    @Json(name = "bibleId")
    var bibleId: Int? = 0,
    @Json(name = "bookId")
    var bookId: Int? = 0,
    @Json(name = "bookRecordId")
    var bookRecordId: String? = "",
    @Json(name = "chapterIntro")
    var chapterIntro: String? = "",
    @Json(name = "chapters")
    var chapters: List<BibleChapterPropertyResponseData> = listOf(),
) : Parcelable