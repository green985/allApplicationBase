package com.oyetech.remote.radioRemote.dataSourcee

import com.oyetech.models.postBody.broken.BasicPostBody
import com.oyetech.models.postBody.uuid.uuid.StationUuidPostBody
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.remote.RadioApiService
import com.oyetech.remote.helper.interceptTrueForm
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-29.11.2022-
-00:58-
 **/

class StationListDataSource(private var radioApiService: RadioApiService) {

    var brokenBasicPostBody = BasicPostBody()

    fun getLastClickStationList(): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getLastClickStationList(brokenBasicPostBody)
        }
    }

    fun getLastChangeStationList(): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getLastChangeStationList(brokenBasicPostBody)
        }
    }

    fun getTopVotedStationList(): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getTopVotedStationList(brokenBasicPostBody)
        }
    }

    fun getTopClickStationList(): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getTopClickStationList(brokenBasicPostBody)
        }
    }

    fun getStationListWithTagParams(tagString: String): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getStationListWithTagParams(brokenBasicPostBody, tagString)
        }
    }

    fun getStationListWithSearchParams(searchString: String): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getStationListWithSearchParams(brokenBasicPostBody, searchString)
        }
    }

    fun getStationListWithUuid(postBody: StationUuidPostBody): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getStationListWithUuid(postBody)
        }
    }

    fun getStationListWithLanguageParams(languageString: String): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getStationListWithLanguageParams(brokenBasicPostBody, languageString)
        }
    }

    fun getStationListWithCountryParams(countryString: String): Flow<List<RadioStationResponseData>> {
        return interceptTrueForm {
            radioApiService.getStationListWithCountryParams(brokenBasicPostBody, countryString)
        }
    }

}