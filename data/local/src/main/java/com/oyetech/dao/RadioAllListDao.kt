package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-2.12.2024-
-22:25-
 **/

@Dao
interface RadioAllListDao : BaseDao<RadioStationResponseData> {

    @Query("select * FROM radioDataModel " + "WHERE stationuuid = :stationUuid")
    fun findRadioStationWithStationId(stationUuid: String): RadioStationResponseData?

    @Query("SELECT * FROM radioDataModel " + " ")
    fun getRadioLastList(): List<RadioStationResponseData>?

    @Query("delete FROM radioDataModel " + "WHERE stationuuid in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM radioDataModel ")
    fun deleteAllList()

    @Query("select * FROM radioDataModel " + "WHERE stationuuid in (:stationUuidList)")
    fun findRadioStationWithStationIdList(stationUuidList: List<String>): List<RadioStationResponseData>

    fun clearLastListTable() {
        var list = getRadioLastList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteLastList(list.map { it.stationuuid })
    }

    @Transaction
    fun insertLastList(list: List<RadioStationResponseData>) {
//        deleteAllList()
        insert(list)
    }


}