package com.oyetech.models.postBody.biography

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class SetBiographyRequestBody(
    @Json(name = "biographyText") var biographyText: String = "",
) : Parcelable
