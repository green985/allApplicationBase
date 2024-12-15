package com.oyetech.repository.imp

import com.oyetech.dao.RadioAllListDao
import com.oyetech.dao.RadioFavListDao
import com.oyetech.dao.RadioHistoryDao
import com.oyetech.dao.RadioLastListDao
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.radioDataRepositories.RadioDataOperationRepository
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

/**
Created by Erdi Özbek
-24.11.2022-
-01:03-
 **/

class RadioDataOperationRepositoryImp(
    var radioHistoryDao: RadioHistoryDao,
    var radioLastListDao: RadioLastListDao,
    var radioFavListDao: RadioFavListDao,
    var radioAllListDao: RadioAllListDao,
    var sharedOperationRepository: SharedOperationRepository,
) : RadioDataOperationRepository {

    override fun getRadioHistoryDataFlow(): Flow<List<RadioStationResponseData>> {
        return radioHistoryDao.getHistoryRadioModelListFlow()
    }

    override fun saveHistory(lastList: List<RadioStationResponseData>) {
        radioHistoryDao.insertLastHistoryList(lastList)
    }

    override fun saveLastRadioList(lastList: List<RadioStationResponseData>) {
        // radioLastListDao.clearLastListTable()
        radioAllListDao.insert(lastList)
        radioLastListDao.insertLastList(lastList)
    }

    override fun getLastRadioList(): List<RadioStationResponseData> {
        return radioLastListDao.getRadioLastList() ?: emptyList()
    }

    override fun getRadioFavList(): List<RadioStationFavModel> {
        var favModelList = radioFavListDao.getRadioFavList()
        return favModelList ?: emptyList()
    }

    override fun getRadioFavListFlow(): Flow<List<RadioStationFavModel>> {
        return radioFavListDao.getRadioFavListFlow() ?: flowOf()
    }

    override fun addToFavList(radioModel: RadioStationResponseData) {
        var radioStationFavModel = RadioStationFavModel(radioModel.stationuuid)
        radioFavListDao.addToFavList(radioStationFavModel)
    }

    override fun removeToFavList(radioModel: RadioStationResponseData) {
        radioFavListDao.removeFav(radioModel.stationuuid)
    }

    override fun addToHistory(radioModel: RadioStationResponseData) {

        radioHistoryDao.addToHistory(listOf(radioModel))
    }

    override fun getNextRadioStation(): RadioStationResponseData? {
        var lastStationId = getLastStationId() ?: return null
        var lastList = getLastRadioList()
        var foundModel = lastList.find {
            it.stationuuid == lastStationId
        }
        Timber.d("first found model ==  " + foundModel)

        var foundedModelIndex = lastList.indexOf(foundModel)

        Timber.d("founded index == " + foundedModelIndex)

        if (foundedModelIndex == -1) {
            Timber.d("not foundddd == ")
            return null
        }
        if (foundedModelIndex == lastList.size - 1) {
            Timber.d("last radio, return first")
            return lastList[0]
        }
        var model = lastList.get(foundedModelIndex + 1)

        Timber.d("modellll == " + model)

        return model
    }

    override fun getPreviousRadioStation(): RadioStationResponseData? {
        var lastStationId = getLastStationId() ?: return null
        var lastList = getLastRadioList()
        var foundModel = lastList.find {
            it.stationuuid == lastStationId
        }
        Timber.d("first found model ==  " + foundModel)

        var foundedModelIndex = lastList.indexOf(foundModel)

        Timber.d("founded index == " + foundedModelIndex)

        if (foundedModelIndex == -1) {
            Timber.d("not foundddd == ")
            return null
        }
        if (foundedModelIndex == 0) {
            Timber.d("first radio, return last")
            return lastList[lastList.size - 1]
        }
        var model = lastList.get(foundedModelIndex - 1)

        Timber.d("modellll == " + model)

        return model
    }

    private fun getLastStationId(): String? {
        Timber.d("last station name == " + lastRadioData?.radioName)

        var lastRadioStationId = lastRadioData?.stationuuid
        if (lastRadioStationId == null) {
            Timber.d("station id == null")
        }
        return lastRadioStationId
    }

    override fun findRadioStationWithStationId(stationUuid: String): RadioStationResponseData? {
        return radioAllListDao.findRadioStationWithStationId(stationUuid)
    }

    override fun clearHistory() {
        radioHistoryDao.clearHistoryTable()
        radioHistoryDao.addToHistory(listOf(RadioStationResponseData("0")))
    }

    override fun removeFromHistoryList(list: List<RadioStationResponseData>) {
        radioHistoryDao.deleteHistoryList(list.map { it.stationuuid })
    }

    override fun clearFavList() {
        radioFavListDao.deleteAllList()
        radioFavListDao.addToFavList(RadioStationFavModel("0"))
    }

    fun getLastRadioDataa(): RadioStationResponseData? {
        return sharedOperationRepository.getSavedLastRadio()
    }

    fun setLastRadioDataa(value: RadioStationResponseData?) {
        if (value != null) {
            sharedOperationRepository.saveLastRadioToDB(value)
        }
    }

    override fun getRadioWithStationUuidList(stationUuidList: List<String>): List<RadioStationResponseData> {
        return radioAllListDao.findRadioStationWithStationIdList(stationUuidList)
    }

    override fun getRadioSingleFav(stationUuid: String): RadioStationFavModel? {
        return radioFavListDao.getRadioSingleFav(stationUuid)
    }

    override var lastRadioData: RadioStationResponseData? =
        sharedOperationRepository.getSavedLastRadio()
        get() = sharedOperationRepository.getSavedLastRadio()
        set(value) {
            if (value != null) {
                sharedOperationRepository.saveLastRadioToDB(value)
            }
            field = value
        }

    fun getRadioListWithParams(params: String = "") {

    }

}