package com.oyetech.models.postBody.bibles

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-4.11.2023-
-15:52-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleListRequestBody(
    @Json(name = "country") var countryCode: String = "",
) : Parcelable
