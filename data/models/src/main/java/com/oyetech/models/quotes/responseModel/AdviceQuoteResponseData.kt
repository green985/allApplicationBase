package com.oyetech.models.quotes.responseModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

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
    val quoteText: String,
    @PrimaryKey
    var quoteId: String = quoteText.toMd5WithFixedLength(),
    val author: String,
    val tags: List<String>,
    val noteToInspector: String? = null,
) : Parcelable
