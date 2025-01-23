package com.oyetech.domain.radioOperationUseCases.remoteUseCase

import com.oyetech.domain.repository.radioDataRepositories.RadioDataOperationRepository
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.utils.helper.TimeFunctions
import com.oyetech.tools.coroutineHelper.launchCustom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-22.11.2022-
-17:43-
 **/

class RadioDataOperationUseCase(
    private var radioDataOperationRepository: RadioDataOperationRepository,

    ) {

    var lastRadioDataa = radioDataOperationRepository.lastRadioData

    suspend fun getRadioHistoryFlowList(): Flow<List<RadioStationResponseData>> {
        return radioDataOperationRepository.getRadioHistoryDataFlow()
    }

    suspend fun getRadioLastList(): List<RadioStationResponseData> {
        return radioDataOperationRepository.getLastRadioList()
    }

    suspend fun saveLastRadioList(lastList: List<RadioStationResponseData>) {
        return radioDataOperationRepository.saveLastRadioList(lastList)
    }

    fun saveHistoryList(lastList: List<RadioStationResponseData>) {
        GlobalScope.launchCustom {
            radioDataOperationRepository.saveHistory(lastList)
        }
    }

    fun clearHistoryList() {
        GlobalScope.launchCustom {
            radioDataOperationRepository.clearHistory()
        }
    }

    fun removeFromHistoryList(list: List<RadioStationResponseData>) {
        GlobalScope.launchCustom {
            radioDataOperationRepository.removeFromHistoryList(list)
        }
    }

    fun clearFavList() {
        GlobalScope.launchCustom {
            radioDataOperationRepository.clearFavList()
        }
    }

    fun getLastRadioData(): RadioStationResponseData? {
        return radioDataOperationRepository.lastRadioData
    }

    fun saveToHistory(radioModel: RadioStationResponseData) {
        radioDataOperationRepository.addToHistory(radioModel)
    }

    fun removeToFavList(radioModel: RadioStationResponseData) {
        radioDataOperationRepository.removeToFavList(radioModel)
    }

    fun addToFavList(radioModel: RadioStationResponseData) {
        radioDataOperationRepository.addToFavList(radioModel)
    }

    suspend fun prepareRadioDataOperation(
        radioModel: RadioStationResponseData,
        radioListFromLiveData: List<RadioStationResponseData>?,
    ) {
        radioDataOperationRepository.lastRadioData = radioModel
        saveToHistory(radioModel.copy(historyClickTimeMilis = TimeFunctions.getTimeMilis()))
        if (radioListFromLiveData?.isNotEmpty() == true) {
            saveLastRadioList(radioListFromLiveData)
        }
    }

    fun getNextRadioStation(): RadioStationResponseData? {
        return radioDataOperationRepository.getNextRadioStation()
    }

    fun getRadioFavListFlow(): Flow<List<RadioStationFavModel>> {
        return radioDataOperationRepository.getRadioFavListFlow()
    }

    suspend fun getRadioFavList(): List<RadioStationFavModel> {
        return radioDataOperationRepository.getRadioFavList()
    }

    suspend fun getRadioFavWithStationUuid(stationUuid: String): RadioStationFavModel? {
        return radioDataOperationRepository.getRadioSingleFav(stationUuid)
    }

    fun getPreviousRadioStation(): RadioStationResponseData? {

        return radioDataOperationRepository.getPreviousRadioStation()
    }

    suspend fun getRadioWithStationUuid(stationUuid: String): RadioStationResponseData? {
        return radioDataOperationRepository.findRadioStationWithStationId(stationUuid)
    }
    suspend fun getRadioWithStationUuidList(stationUuid: List<String>): List<RadioStationResponseData> {
        return radioDataOperationRepository.getRadioWithStationUuidList(stationUuid)
    }
}