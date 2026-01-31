package com.oyetech.models.radioProject.entity.radioEntity.station

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
Created by Erdi Özbek
-8.12.2022-
-22:58-
 **/

@Entity(tableName = "radioFavModel")
@Keep
data class RadioStationFavModel(

    @PrimaryKey
    var stationUuid: String = "",

    )