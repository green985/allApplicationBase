package com.oyetech.models.radioEntity.station

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.IsPlayingBaseData
import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-6.12.2022-
-16:29-
 **/

@Keep
@Parcelize
data class RadioStationResponse(
    // @PrimaryKey(autoGenerate = true) var rowId: Long = 0,
    @Json(name = "bitrate")
    var bitrate: Int = 0,
    @Json(name = "changeuuid")
    var changeuuid: String = "",
    @Json(name = "clickcount")
    var clickcount: Int = 0,
    @Json(name = "clicktimestamp")
    var clicktimestamp: String = "",
    @Json(name = "clicktimestamp_iso8601")
    var clicktimestampIso8601: String = "",
    @Json(name = "clicktrend")
    var clicktrend: Int = 0,
    @Json(name = "codec")
    var codec: String = "",
    @Json(name = "country")
    var country: String = "",
    @Json(name = "countrycode")
    var countrycode: String = "",
    @Json(name = "favicon")
    var favicon: String = "",
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
    @Json(name = "name")
    var radioName: String = "",
    @Json(name = "ssl_error")
    var sslError: Int = 0,
    @Json(name = "state")
    var state: String = "",

    @PrimaryKey
    @Json(name = "stationuuid")
    var stationuuid: String = "",
    @Json(name = "tags")
    var tags: String = "",
    @Json(name = "url")
    var url: String = "",
    @Json(name = "url_resolved")
    var radioStreamUrl: String = "",
    @Json(name = "votes")
    var votes: Int = 0,

    @Ignore
    var isPlaying: Boolean = false,

    @Ignore
    var isFavorite: Boolean = false,
    @Ignore
    var isExpand: Boolean = false,

    @Transient
    var historyClickTimeMilis: Long = 0,

    @Ignore
    @IgnoredOnParcel
    @Transient
    var mappBody: RadioStationResponseData? = null,

    @Ignore
    @IgnoredOnParcel
    @Transient
    var radioTitle: String = "",
) : Parcelable, IsPlayingBaseData(
    _isPlaying = isPlaying,
    _isFavoriteView = isFavorite,
    radioStationUUID = stationuuid
)

fun RadioStationResponseData.mapToResponseData(): RadioStationResponse {
    var data = RadioStationResponse(
        /*

                bitrate = this.bitrate,
                codec = this.codec,
                language = this.language,
                languagecodes = this.languagecodes,
                state = this.state,
                tags = this.tags,
                historyClickTimeMilis = this.historyClickTimeMilis,
                votes = this.votes,
                radioTitle = this.radioTitle,
                mappBody = this,
        clickcount = this.clickcount,
        country = this.country,
        countrycode = this.countrycode,
        favicon = this.favicon,
        radioName = this.radioName,
        stationuuid = this.stationuuid,
        url = this.url,
        radioStreamUrl = this.radioStreamUrl,


         */
    )

    return data
}