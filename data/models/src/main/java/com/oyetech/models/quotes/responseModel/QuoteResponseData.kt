package com.oyetech.models.quotes.responseModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.firebase.firestore.Exclude
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.security.MessageDigest
import java.util.Date

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

    @Json(name = "q")
    var text: String = "",
    @PrimaryKey
    var quoteId: String = text.toMd5WithFixedLength(),
    @Json(name = "createdAt")
    var createdAt: Long = Date().time,

    @get:Exclude
    var isSeen: Boolean = false,

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

fun String.toMd5WithFixedLength(): String {
    // Convert string to MD5 hash
    val md5Hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        .joinToString("") { "%02x".format(it) }

    // Ensure the resulting string is exactly 40 characters
    return if (md5Hash.length >= 40) {
        md5Hash.substring(0, 40)
    } else {
        md5Hash.padEnd(40, '-')
    }
}

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