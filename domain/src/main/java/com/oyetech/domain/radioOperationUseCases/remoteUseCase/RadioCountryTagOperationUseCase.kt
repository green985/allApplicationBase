package com.oyetech.domain.useCases.remoteUseCase

import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioCountryTagListRepository
import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-2.12.2022-
-17:20-
 **/

class RadioCountryTagOperationUseCase(private var repository: RadioCountryTagListRepository) {

    fun getTagListWithSorted(): Flow<List<TagResponseData>> {
        return repository.getTagList().map {
            it.sortedByDescending {
                it.stationcount
            }
        }
    }

    fun getCountryList(): Flow<List<CountryResponseData>> {
        return repository.getCountryList().map {
            it.sortedByDescending {
                it.stationcount
            }
        }
    }

    fun getLanguagesList(): Flow<List<LanguageResponseData>> {
        return repository.getLanguagesList().map {
            it.sortedByDescending {
                it.stationcount
            }
        }
    }

}