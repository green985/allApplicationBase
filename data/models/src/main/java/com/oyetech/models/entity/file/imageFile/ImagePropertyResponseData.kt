package com.oyetech.models.entity.file.imageFile

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ImagePropertyResponseData(
    @Json(name = "height") var height: Int = 0,
    @Json(name = "width") var width: Int = 0,
) : Parcelable
