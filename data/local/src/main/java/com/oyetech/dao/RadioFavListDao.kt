package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-8.12.2022-
-22:40-
 **/

@Dao
interface RadioFavListDao : BaseDao<RadioStationFavModel> {

    @Query("SELECT * FROM radioFavModel " + " ")
    fun getRadioFavList(): List<RadioStationFavModel>?

    @Query("SELECT * FROM radioFavModel " + " ")
    fun getRadioFavListFlow(): Flow<List<RadioStationFavModel>>?

    @Query("delete FROM radioFavModel " + "WHERE stationuuid in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM radioFavModel ")
    fun deleteAllList()

    @Query("delete FROM radioFavModel " + "WHERE stationuuid = :stationuuid")
    fun removeFav(stationuuid: String)

    @Query("SELECT * FROM radioFavModel " + "WHERE stationuuid = :stationUuid")
    fun getRadioSingleFav(stationUuid: String): RadioStationFavModel?

    fun addToFavList(radioFavModel: RadioStationFavModel) {
        insert(radioFavModel)
    }

    @Transaction
    fun clearLastListTable() {
        var list = getRadioFavList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteLastList(list.map { it.stationUuid })
    }

    @Transaction
    fun insertLastList(list: List<RadioStationFavModel>) {
        deleteAllList()
        insert(list)
    }
}