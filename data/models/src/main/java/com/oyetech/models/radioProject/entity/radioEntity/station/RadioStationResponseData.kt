package com.oyetech.models.radioProject.entity.radioEntity.station

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "radioDataModel")
@Keep
data class RadioStationResponseData(

    @PrimaryKey
    var stationuuid: String = "",

    var clicktimestamp: String = "",

    var clicktimestampIso8601: String = "",

    var clicktrend: Int = 0,

    var codec: String = "",

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

    var sslError: Int = 0,

    var state: String = "",

    var tags: String = "",

    var bitrate: Int = 0,

    var votes: Int = 0,

    var changeuuid: String = "",

    var clickcount: Int = 0,

    var country: String = "",

    var countrycode: String = "",

    var favicon: String = "",

    var radioName: String = "",

    var url: String = "",

    var radioStreamUrl: String = "",

    var isPlaying: Boolean = false,

    var historyClickTimeMilis: Long = 0,

    var radioTitle: String = "",
)
