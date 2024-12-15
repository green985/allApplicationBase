package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Query
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-24.11.2022-
-01:43-
 **/

@Dao
interface RadioHistoryDao : BaseDao<RadioStationResponseData> {

    @Query("SELECT * FROM radioDataModel " + "WHERE stationuuid  IS NOT :asd  ORDER BY historyClickTimeMilis DESC LIMIT 50")
    fun getHistoryRadioModelListFlow(asd: String = ""): Flow<List<RadioStationResponseData>>

    @Query("SELECT * FROM radioDataModel " + "ORDER BY radioName ")
    fun getRadioHistoryList(): List<RadioStationResponseData>?

    @Query("delete FROM radioDataModel " + "WHERE stationuuid in (:idList)")
    fun deleteHistoryList(idList: List<String>): Int

    fun clearHistoryTable() {
        var list = getRadioHistoryList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteHistoryList(list.map { it.stationuuid })
    }

    fun insertLastHistoryList(list: List<RadioStationResponseData>) {
        insert(list)
    }

    fun addToHistory(list: List<RadioStationResponseData>) {
        insert(list)
    }
}