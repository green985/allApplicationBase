package com.oyetech.models.entity.bibleProperties

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleBookPropertyResponseData(
    @Json(name = "abbreviation")
    var abbreviation: String = "",
    @Json(name = "bibleId")
    var bibleId: Int = 0,
    @Json(name = "bookId")
    var bookId: Int = 0,
    @Json(name = "bookRecordId")
    var bookRecordId: String = "",
    @Json(name = "name")
    var name: String = "",
    @Json(name = "nameLong")
    var nameLong: String = "",
    @Json(name = "order")
    var order: Int = 0,
    @Json(name = "chapterCount")
    var chapterCount: Int = 0,
    @Json(name = "isRead")
    var isRead: Boolean = false,
) : Parcelable