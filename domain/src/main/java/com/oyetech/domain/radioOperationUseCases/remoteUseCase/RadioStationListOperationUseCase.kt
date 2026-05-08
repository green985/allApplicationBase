package com.oyetech.domain.radioOperationUseCases.remoteUseCase

import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioStationListRepository
import com.oyetech.models.postBody.uuid.uuid.StationUuidPostBody
import com.oyetech.models.postBody.uuid.uuid.generateStationUuidClass
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
Created by Erdi Özbek
-29.11.2022-
-01:13-
 **/

class RadioStationListOperationUseCase(
    var repository: RadioStationListRepository,
) {

    var sortOperationClickHandler = MutableStateFlow(false)

    var sortOperationClickNavigator = MutableSharedFlow<String>(1)

    fun getSortOperationClickNavigatorStateFlow() = sortOperationClickNavigator.asSharedFlow()

    suspend fun triggerSortOperationClickNavigator(listType: String) {
        sortOperationClickNavigator.emit("")
        sortOperationClickNavigator.emit(listType)
    }

    fun getLastClickStationList(): Flow<List<RadioStationResponseData>> {
        return repository.getLastClickStationList()
    }

    fun getLastChangeStationList(): Flow<List<RadioStationResponseData>> {
        return repository.getLastChangeStationList()
    }

    fun getTopVotedStationList(): Flow<List<RadioStationResponseData>> {
        return repository.getTopVotedStationList()
    }

    fun getTopClickStationList(): Flow<List<RadioStationResponseData>> {
        return repository.getTopClickStationList()
    }

    fun getStationListWithTagParams(tagString: String): Flow<List<RadioStationResponseData>> {
        return repository.getStationListWithTagParams(tagString)
    }

    fun getStationListWithSearchParams(searchString: String): Flow<List<RadioStationResponseData>> {
        return repository.getStationListWithSearchParams(searchString)
    }

    fun getStationListWithLanguageParams(languageString: String): Flow<List<RadioStationResponseData>> {
        return repository.getStationListWithLanguageParams(languageString)
    }

    fun getStationListWithCountryParams(countryString: String): Flow<List<RadioStationResponseData>> {
        return repository.getStationListWithCountryParams(countryString)
    }

    fun getStationListWithUuid(listUUid: List<String>): Flow<List<RadioStationResponseData>> {
        return flow {
            val postBody = StationUuidPostBody().generateStationUuidClass(listUUid)
            emit(repository.getStationListWithUuid(postBody).first())
        }
    }
}
