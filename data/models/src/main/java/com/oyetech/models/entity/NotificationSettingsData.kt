package com.oyetech.models.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-26.12.2023-
-17:14-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationSettingsData(
    @Json(name = "verseOfTheDay") var verseOfTheDay: Boolean = true,
    @Json(name = "prayerOfTheDay") var prayerOfTheDay: Boolean = true,
) : Parcelable
