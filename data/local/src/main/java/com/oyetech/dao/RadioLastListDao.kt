package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-1.12.2022-
-16:26-
 **/

@Dao
interface RadioLastListDao : BaseDao<RadioStationResponseData> {

    @Query("SELECT * FROM radioDataModel " + " ")
    fun getRadioLastList(): List<RadioStationResponseData>?

    @Query("delete FROM radioDataModel " + "WHERE stationuuid in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM radioDataModel ")
    fun deleteAllList()

    fun clearLastListTable() {
        var list = getRadioLastList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteLastList(list.map { it.stationuuid })
    }

    @Transaction
    fun insertLastList(list: List<RadioStationResponseData>) {
        deleteAllList()
        insert(list)
    }
}