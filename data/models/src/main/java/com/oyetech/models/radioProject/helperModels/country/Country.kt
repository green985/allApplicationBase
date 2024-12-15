package com.oyetech.models.radioProject.helperModels.country

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class Country(
    @Json(name = "name") var name: String = "",
    @Json(name = "code") var code: String = "",
) : Parcelable