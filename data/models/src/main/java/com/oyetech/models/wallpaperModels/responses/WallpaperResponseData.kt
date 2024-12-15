package com.oyetech.models.wallpaperModels.responses

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class WallpaperResponseData(
    @Json(name = "id") val id: String,
    @Json(name = "url") val url: String,
    @Json(name = "short_url") val short_url: String,
    @Json(name = "views") val views: Int,
    @Json(name = "favorites") val favorites: Int,
    @Json(name = "source") val source: String,
    @Json(name = "purity") val purity: String,
    @Json(name = "category") val category: String,
    @Json(name = "dimension_x") val dimension_x: Int,
    @Json(name = "dimension_y") val dimension_y: Int,
    @Json(name = "resolution") val resolution: String,
    @Json(name = "ratio") val ratio: String,
    @Json(name = "file_size") val file_size: Long,
    @Json(name = "file_type") val file_type: String,
    @Json(name = "created_at") val created_at: String,
    @Json(name = "colors") val colors: List<String>,
    @Json(name = "path") val path: String,
    @Json(name = "thumbs") val thumbs: ThumbsResponseData,
) : Parcelable {

    fun getFileSizeString(): String {
        return bytesToMegabytes(file_size)
    }

    private fun bytesToMegabytes(bytes: Long): String {
        val megabytes = bytes.toDouble() / (1024 * 1024)
        return "%.2f MB".format(megabytes)
    }
}