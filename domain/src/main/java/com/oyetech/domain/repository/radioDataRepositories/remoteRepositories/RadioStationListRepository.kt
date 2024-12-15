package com.oyetech.domain.repository.radioDataRepositories.remoteRepositories

import com.oyetech.models.postBody.uuid.uuid.StationUuidPostBody
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-29.11.2022-
-01:07-
 **/

interface RadioStationListRepository {

    fun getLastClickStationList(): Flow<List<RadioStationResponseData>>
    fun getLastChangeStationList(): Flow<List<RadioStationResponseData>>
    fun getTopVotedStationList(): Flow<List<RadioStationResponseData>>
    fun getTopClickStationList(): Flow<List<RadioStationResponseData>>

    fun getStationListWithTagParams(tagString: String): Flow<List<RadioStationResponseData>>
    fun getStationListWithLanguageParams(languageString: String): Flow<List<RadioStationResponseData>>
    fun getStationListWithCountryParams(countryString: String): Flow<List<RadioStationResponseData>>
    fun getStationListWithSearchParams(searchString: String): Flow<List<RadioStationResponseData>>
    fun getStationListWithUuid(postBody: StationUuidPostBody): Flow<List<RadioStationResponseData>>
}