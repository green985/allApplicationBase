package com.oyetech.models.helperModels.wallpaper

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-28.02.2024-
-12:20-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class WallpaperCategoryProperty(
    @Json(name = "tagName") val tagName: String,
    @Json(name = "category") val category: String,
    @Json(name = "tagId") val tagId: String,
) : Parcelable
