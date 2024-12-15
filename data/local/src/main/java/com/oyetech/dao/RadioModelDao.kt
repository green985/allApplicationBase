package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Query
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-23.11.2022-
-19:24-
 **/

@Dao
interface RadioModelDao : BaseDao<RadioStationResponseData> {

    @Query("SELECT * FROM radioDataModel " + "ORDER BY radioName ")
    fun getLastRadio(): List<RadioStationResponseData>

}