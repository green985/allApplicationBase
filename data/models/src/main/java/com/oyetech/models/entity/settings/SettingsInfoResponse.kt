package com.oyetech.models.entity.settings

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class SettingsInfoResponse(
    @Json(name = "adSettings")
    var adSettings: AdSettings = AdSettings(),
    @Json(name = "version")
    var version: AndroidVersionResponse = AndroidVersionResponse()
) : Parcelable
