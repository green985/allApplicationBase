package com.oyetech.models.entity.audioBibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioBiblePropertyResponseData(
    @Json(name = "description")
    var description: String? = "",
    @Json(name = "descriptionLocal")
    var descriptionLocal: String? = "",
    @Json(name = "id")
    var id: String? = "",
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "nameLocal")
    var nameLocal: String? = "",
) : Parcelable