package com.oyetech.repository.wallpaperImp.countries

import com.oyetech.domain.repository.LanguageCountryOperationRepository
import com.oyetech.models.entity.language.country.CountryListDataResponse
import com.oyetech.models.entity.language.world.LanguagesListDataResponse
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.remote.helper.interceptGenericResponseTrueForm
import com.oyetech.remote.wallpaperRemote.dataSource.LanguageCountryDataSource
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-30.10.2023-
-23:05-
 **/

class LanguageCountryOperationRepositoryImp(private var languageCountryDataSource: LanguageCountryDataSource) :
    LanguageCountryOperationRepository {

    override fun getLanguages(languageCodeRequestBody: LanguageCodeRequestBody): Flow<LanguagesListDataResponse> {
        return interceptGenericResponseTrueForm {
            languageCountryDataSource.getLanguages(languageCodeRequestBody)
        }
    }

    override fun getCountries(languageCodeRequestBody: LanguageCodeRequestBody): Flow<CountryListDataResponse> {
        return interceptGenericResponseTrueForm {
            languageCountryDataSource.getCountries(languageCodeRequestBody)
        }
    }

}