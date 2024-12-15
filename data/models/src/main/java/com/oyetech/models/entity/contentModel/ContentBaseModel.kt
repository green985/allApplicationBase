package com.oyetech.models.entity.contentModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-13.10.2023-
-16:26-
 **/

@Entity(
    // tableName = "radioDataModel"
)
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ContentBaseModel<T : Parcelable>(
    @Json(name = "data")

    var resultObject: T? = null,

    @PrimaryKey
    @Json(name = "stationuuid")
    var stationuuid: String = "",

    @Json(name = "url")
    var url: String = "",

    @Json(name = "url_resolved")
    var radioStreamUrl: String = "",

    @Json(name = "name")
    var radioName: String = "",

    @Json(name = "favicon")
    var favicon: String = "",

    @Json(name = "country")
    var country: String = "",

    @Json(name = "countrycode")
    var countrycode: String = "",

    @Json(name = "changeuuid")
    var changeuuid: String = "",

    @Json(name = "clickcount")
    var clickcount: Int = 0,

    var isPlaying: Boolean = false,

    var historyClickTimeMilis: Long = 0,

    @Ignore
    @IgnoredOnParcel
    @Transient
    var radioTitle: String = "",

    ) : Parcelable




