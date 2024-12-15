package com.oyetech.models.radioProject.entity.radioEntity.station

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "radioDataModel"
)
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class RadioStationResponseData(
    // @PrimaryKey(autoGenerate = true) var rowId: Long = 0,

    @Json(name = "clicktimestamp")
    var clicktimestamp: String = "",
    @Json(name = "clicktimestamp_iso8601")
    var clicktimestampIso8601: String = "",
    @Json(name = "clicktrend")
    var clicktrend: Int = 0,
    @Json(name = "codec")
    var codec: String = "",
    @Json(name = "geo_lat")
    var geoLat: Double = 0.0,
    @Json(name = "geo_long")
    var geoLong: Double = 0.0,
    @Json(name = "has_extended_info")
    var hasExtendedInfo: Boolean = false,
    @Json(name = "hls")
    var hls: Int = 0,
    @Json(name = "homepage")
    var homepage: String = "",
    @Json(name = "iso_3166_2")
    var iso31662: String = "",
    @Json(name = "language")
    var language: String = "",
    @Json(name = "languagecodes")
    var languagecodes: String = "",
    @Json(name = "lastchangetime")
    var lastchangetime: String = "",
    @Json(name = "lastchangetime_iso8601")
    var lastchangetimeIso8601: String = "",
    @Json(name = "lastcheckok")
    var lastcheckok: Int = 0,
    @Json(name = "lastcheckoktime")
    var lastcheckoktime: String = "",
    @Json(name = "lastcheckoktime_iso8601")
    var lastcheckoktimeIso8601: String = "",
    @Json(name = "lastchecktime")
    var lastchecktime: String = "",
    @Json(name = "lastchecktime_iso8601")
    var lastchecktimeIso8601: String = "",
    @Json(name = "lastlocalchecktime")
    var lastlocalchecktime: String = "",

    @Json(name = "ssl_error")
    var sslError: Int = 0,
    @Json(name = "state")
    var state: String = "",

    @Json(name = "tags")
    var tags: String = "",

    @Json(name = "bitrate")
    var bitrate: Int = 0,

    @Json(name = "votes")
    var votes: Int = 0,

    @PrimaryKey
    @Json(name = "stationuuid")
    var stationuuid: String = "",

    @Json(name = "changeuuid")
    var changeuuid: String = "",
    @Json(name = "clickcount")
    var clickcount: Int = 0,

    @Json(name = "country")
    var country: String = "",
    @Json(name = "countrycode")
    var countrycode: String = "",

    @Json(name = "favicon")
    var favicon: String = "",
    @Json(name = "name")
    var radioName: String = "",

    @Json(name = "url")
    var url: String = "",
    @Json(name = "url_resolved")
    var radioStreamUrl: String = "",

    var isPlaying: Boolean = false,

    var historyClickTimeMilis: Long = 0,

    @Ignore
    @IgnoredOnParcel
    @Transient
    var radioTitle: String = "",

    ) : Parcelable

