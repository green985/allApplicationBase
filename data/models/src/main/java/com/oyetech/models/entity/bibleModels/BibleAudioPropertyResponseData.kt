package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleAudioPropertyResponseData(
    @Json(name = "audioInfoDetail")
    var audioFileDetailList: List<BibleAudioFileDetailResponseData> = listOf(),
    @Json(name = "chapterId")
    var chapterId: Int = 0,
    @Json(name = "verseNumber")
    var verseNumber: Int = 0,
) : Parcelable