package com.oyetech.models.entity.bibleProperties

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleVerseNoteResponseData(
    @Json(name = "noteContent")
    var noteContent: String = "",
    @Json(name = "noteTag")
    var noteTag: String = "",
) : Parcelable