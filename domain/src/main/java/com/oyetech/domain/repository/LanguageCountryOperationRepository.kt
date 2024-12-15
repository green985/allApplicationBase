package com.oyetech.domain.repository

import com.oyetech.models.entity.language.country.CountryListDataResponse
import com.oyetech.models.entity.language.world.LanguagesListDataResponse
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-30.10.2023-
-23:10-
 **/

interface LanguageCountryOperationRepository {
    fun getLanguages(languageCodeRequestBody: LanguageCodeRequestBody): Flow<LanguagesListDataResponse>
    fun getCountries(languageCodeRequestBody: LanguageCodeRequestBody): Flow<CountryListDataResponse>
}