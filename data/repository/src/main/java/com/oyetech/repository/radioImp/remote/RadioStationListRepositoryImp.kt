package com.oyetech.repository.imp.remote

import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioStationListRepository
import com.oyetech.models.postBody.uuid.uuid.StationUuidPostBody
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.remote.radioRemote.dataSourcee.StationListDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-29.11.2022-
-01:09-
 **/

class RadioStationListRepositoryImp(private var radioListDataSource: StationListDataSource) :
    RadioStationListRepository {

    override fun getLastClickStationList(): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getLastClickStationList()
    }

    override fun getLastChangeStationList(): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getLastChangeStationList()

    }

    override fun getTopVotedStationList(): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getTopVotedStationList()

    }

    override fun getTopClickStationList(): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getTopClickStationList()

    }

    override fun getStationListWithTagParams(tagString: String): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getStationListWithTagParams(tagString).map {
            it.sortedByDescending {
                it.clickcount
            }
        }
    }

    override fun getStationListWithSearchParams(searchString: String): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getStationListWithSearchParams(searchString).map {
            it.sortedByDescending {
                it.clickcount
            }
        }
    }

    override fun getStationListWithUuid(postBody: StationUuidPostBody): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getStationListWithUuid(postBody).map {
            it.sortedByDescending {
                it.clickcount
            }
        }
    }

    override fun getStationListWithLanguageParams(languageString: String): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getStationListWithLanguageParams(languageString).map {
            it.sortedByDescending {
                it.clickcount
            }
        }
    }

    override fun getStationListWithCountryParams(countryString: String): Flow<List<RadioStationResponseData>> {
        return radioListDataSource.getStationListWithCountryParams(countryString).map {
            it.sortedByDescending {
                it.clickcount
            }
        }
    }

}