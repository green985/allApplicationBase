package com.oyetech.models.entity.bibleModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleCountryResponseData(
    @Json(name = "id")
    var id: Int? = 0,
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "nameLocal")
    var nameLocal: String? = "",
    @Json(name = "recordId")
    var recordId: String? = "",

    ) : Parcelable