package com.oyetech.models.quotes.responseModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
Created by Erdi Özbek
-1.02.2025-
-18:17-
 **/

@Entity(
    tableName = "adviceQuoteList"
)
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AdviceQuoteResponseData(
    var quoteText: String = "",
    var documentId: String = "",
    @PrimaryKey
    var quoteId: String = quoteText.toMd5WithFixedLength(),
    var author: String = "",
    var tags: List<String> = emptyList(),
    var noteToInspector: String? = null,
    @Json(name = "createdAt")
    var createdAt: Long = Date().time,
    var status: String = "Idle",
) : Parcelable
