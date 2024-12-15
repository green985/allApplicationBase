package com.oyetech.models.wallpaperModels.helperModels.image

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-3.03.2024-
-15:28-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class ImageViewerPropertyData(
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String,
    @Json(name = "imageId") val imageId: String,
) : Parcelable