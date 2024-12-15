package com.oyetech.models.radioEntity.tag

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-2.12.2022-
-17:12-
 **/
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TagResponseData(
    @Json(name = "name")
    var name: String = "",
    @Json(name = "stationcount")
    var stationcount: Int = 0
) : Parcelable