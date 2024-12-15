package com.oyetech.models.entity.settings

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AndroidVersionResponse(
    @Json(name = "minBuildVersionNumber")
    var minBuildVersionNumber: Int = 0,
    @Json(name = "optionalWarningMinBuildVersionNumber")
    var optionalWarningMinBuildVersionNumber: Int = 0
) : Parcelable
