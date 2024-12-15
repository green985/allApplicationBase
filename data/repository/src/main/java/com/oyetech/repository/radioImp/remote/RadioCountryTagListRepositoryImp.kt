package com.oyetech.repository.imp.remote

import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioCountryTagListRepository
import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import com.oyetech.remote.radioRemote.dataSourcee.CountryTagDataSource
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-2.12.2022-
-17:16-
 **/

class RadioCountryTagListRepositoryImp(private var countryTagDataSource: CountryTagDataSource) :
    RadioCountryTagListRepository {

    override fun getTagList(): Flow<List<TagResponseData>> {
        return countryTagDataSource.getTagList()
    }

    override fun getCountryList(): Flow<List<CountryResponseData>> {
        return countryTagDataSource.getCountryList()

    }

    override fun getLanguagesList(): Flow<List<LanguageResponseData>> {
        return countryTagDataSource.getLanguagesList()

    }


}