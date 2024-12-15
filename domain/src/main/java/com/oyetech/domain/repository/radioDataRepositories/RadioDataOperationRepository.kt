package com.oyetech.domain.repository.radioDataRepositories

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-24.11.2022-
-01:24-
 **/

interface RadioDataOperationRepository {

    fun getRadioHistoryDataFlow(): Flow<List<RadioStationResponseData>>

    fun saveHistory(lastList: List<RadioStationResponseData>)

    fun saveLastRadioList(lastList: List<RadioStationResponseData>)

    fun getLastRadioList(): List<RadioStationResponseData>

    fun removeFromHistoryList(list: List<RadioStationResponseData>)

    fun clearHistory()

    var lastRadioData: RadioStationResponseData?

    fun addToHistory(radioModel: RadioStationResponseData)

    fun getNextRadioStation(): RadioStationResponseData?
    fun getPreviousRadioStation(): RadioStationResponseData?

    fun findRadioStationWithStationId(stationUuid: String): RadioStationResponseData?

    fun getRadioFavList(): List<RadioStationFavModel>
    fun getRadioFavListFlow(): Flow<List<RadioStationFavModel>>

    fun clearFavList()

    fun addToFavList(radioModel: RadioStationResponseData)
    fun removeToFavList(radioModel: RadioStationResponseData)
    fun getRadioWithStationUuidList(stationUuid: List<String>): List<RadioStationResponseData>
    fun getRadioSingleFav(stationUuid: String): RadioStationFavModel?
}