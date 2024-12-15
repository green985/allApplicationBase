package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-30.10.2023-
-20:30-
 **/

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class BibleSectionResponse(
    var sectionName: String,
    var sectionId: Long = 0,
) : Parcelable