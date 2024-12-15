package com.oyetech.remote.radioRemote.dataSourcee

import com.oyetech.models.postBody.broken.BasicPostBody
import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import com.oyetech.remote.RadioApiService
import com.oyetech.remote.helper.interceptTrueForm
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-2.12.2022-
-17:14-
 **/

class CountryTagDataSource(private var radioApiService: RadioApiService) {

    var brokenBasicPostBody = BasicPostBody()

    fun getTagList(): Flow<List<TagResponseData>> {
        return interceptTrueForm {
            radioApiService.getTagList(brokenBasicPostBody)
        }
    }

    fun getCountryList(): Flow<List<CountryResponseData>> {
        return interceptTrueForm {
            radioApiService.getCountryList(brokenBasicPostBody)
        }
    }

    fun getLanguagesList(): Flow<List<LanguageResponseData>> {
        return interceptTrueForm {
            radioApiService.getLanguagesList(brokenBasicPostBody)
        }
    }

}