package com.oyetech.models.postBody.uuid.uuid

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel

/**
Created by Erdi Özbek
-8.12.2022-
-23:12-
 **/

data class StationUuidPostBody(
    var uuids: String = "",
)

fun StationUuidPostBody.generateStationUuidClassWithFavList(list: List<RadioStationFavModel>): StationUuidPostBody {
    val stringBuilder = StringBuilder()
    list.forEach {
        stringBuilder.append(it.stationUuid)
        stringBuilder.append(",")
    }

    stringBuilder.dropLast(1)

    this.uuids = stringBuilder.toString()
    return this
}

fun StationUuidPostBody.generateStationUuidClass(list: List<String>): StationUuidPostBody {
    val stringBuilder = StringBuilder()
    list.forEach {
        stringBuilder.append(it)
        stringBuilder.append(",")
    }

    stringBuilder.dropLast(1)

    this.uuids = stringBuilder.toString()
    return this
}
