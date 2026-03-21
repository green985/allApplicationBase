package com.oyetech.models.radioProject.entity.radioEntity.station

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.oyetech.models.radioProject.radioModels.IsPlayingBaseData
import kotlinx.parcelize.IgnoredOnParcel

/**
Created by Erdi Özbek
-6.12.2022-
-16:29-
 **/

@Keep
data class RadioStationResponse(
    // @PrimaryKey(autoGenerate = true) var rowId: Long = 0,
    var bitrate: Int = 0,
    var changeuuid: String = "",
    var clickcount: Int = 0,
    var clicktimestamp: String = "",
    var clicktimestampIso8601: String = "",
    var clicktrend: Int = 0,
    var codec: String = "",
    var country: String = "",
    var countrycode: String = "",
    var favicon: String = "",
    var geoLat: Double = 0.0,
    var geoLong: Double = 0.0,
    var hasExtendedInfo: Boolean = false,
    var hls: Int = 0,
    var homepage: String = "",
    var iso31662: String = "",
    var language: String = "",
    var languagecodes: String = "",
    var lastchangetime: String = "",
    var lastchangetimeIso8601: String = "",
    var lastcheckok: Int = 0,
    var lastcheckoktime: String = "",
    var lastcheckoktimeIso8601: String = "",
    var lastchecktime: String = "",
    var lastchecktimeIso8601: String = "",
    var lastlocalchecktime: String = "",
    var radioName: String = "",
    var sslError: Int = 0,
    var state: String = "",

    @PrimaryKey
    var stationuuid: String = "",
    var tags: String = "",
    var url: String = "",
    var radioStreamUrl: String = "",
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
