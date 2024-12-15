package com.oyetech.domain.repository.radioDataRepositories.remoteRepositories

import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-2.12.2022-
-17:18-
 **/

interface RadioCountryTagListRepository {
    fun getTagList(): Flow<List<TagResponseData>>
    fun getCountryList(): Flow<List<CountryResponseData>>
    fun getLanguagesList(): Flow<List<LanguageResponseData>>
}
