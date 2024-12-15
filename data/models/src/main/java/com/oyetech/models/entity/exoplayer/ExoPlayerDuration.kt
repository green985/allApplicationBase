package com.oyetech.models.entity.exoplayer

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-18.09.2023-
-01:22-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class ExoPlayerDuration(
    @Json(name = "totalDuration") var totalDuration: Long = 0L,
    @Json(name = "currentDuration") var currentDuration: Long = 0L,
) : Parcelable {

}
