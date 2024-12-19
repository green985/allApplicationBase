package com.oyetech.models.quotes.responseModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
Created by Erdi Özbek
-16.12.2024-
-22:18-
 **/

@Entity(
    tableName = "quoteDataModel"
)
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class QuoteResponseData(
//    @Json(name = "quoteId")

    @PrimaryKey
    var quoteId: String = UUID.randomUUID().toString(),

    var isSeen: Boolean = false,

    @Json(name = "q")
    var text: String = "",
    @Json(name = "a")
    var author: String = "",
    @Json(name = "c")
    var charCount: Int = 0,
    @Json(name = "i")
    var authorImage: String = "",
    @Json(name = "htmlFormatted")
    var htmlFormatted: String = "",
    @Json(name = "tags")
    var tags: List<String> = emptyList(),
) : Parcelable

class TagsTypeConverter {

    @TypeConverter
    fun fromTagsList(tags: List<String>?): String {
        return tags?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toTagsList(tagsString: String): List<String> {
        return if (tagsString.isNotEmpty()) {
            tagsString.split(",").map { it.trim() }
        } else {
            emptyList()
        }
    }
}