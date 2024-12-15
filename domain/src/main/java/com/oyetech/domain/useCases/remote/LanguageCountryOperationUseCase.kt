package com.oyetech.domain.useCases.remote

import com.oyetech.domain.repository.LanguageCountryOperationRepository
import com.oyetech.models.entity.language.country.CountryListDataResponse
import com.oyetech.models.entity.language.world.LanguagesListDataResponse
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-30.10.2023-
-23:14-
 **/

class LanguageCountryOperationUseCase(
    private var languageCountryOperationRepository:
    LanguageCountryOperationRepository,
) {

    fun getLanguages(languageCodeRequestBody: LanguageCodeRequestBody): Flow<LanguagesListDataResponse> {
        return languageCountryOperationRepository.getLanguages(languageCodeRequestBody)
    }

    fun getCountries(languageCodeRequestBody: LanguageCodeRequestBody): Flow<CountryListDataResponse> {
        return languageCountryOperationRepository.getCountries(languageCodeRequestBody)
    }


}