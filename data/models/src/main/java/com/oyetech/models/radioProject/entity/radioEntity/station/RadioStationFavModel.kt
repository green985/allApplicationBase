package com.oyetech.models.radioProject.entity.radioEntity.station

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-8.12.2022-
-22:58-
 **/

@Entity(
    tableName = "radioFavModel"
)
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class RadioStationFavModel(

    @PrimaryKey
    @Json(name = "stationuuid")
    var stationUuid: String = ""

) : Parcelable