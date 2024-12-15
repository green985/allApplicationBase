package com.oyetech.domain.useCases

import com.oyetech.domain.repository.WorldOperationRepository
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.entity.language.TranslationDataResponse
import com.oyetech.models.entity.language.country.CountryListDataResponse
import com.oyetech.models.entity.language.translate.TranslateMessageResponse
import com.oyetech.models.entity.language.world.LanguagesListDataResponse
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.models.postBody.world.TranslateOperationRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-17.05.2022-
-01:39-
 **/

class WorldOperationUseCase(private var worldOperationRepository: WorldOperationRepository) {

    suspend fun getApplicationTextResources(
        params: LanguageCodeRequestBody,
    ): Flow<TextResourcesDataResponse> {
        return worldOperationRepository.getApplicationTextResources(params)
    }

    suspend fun getLanguageList(
        params: LanguageCodeRequestBody,
    ): Flow<LanguagesListDataResponse> {
        return worldOperationRepository.getLanguages(params)
    }

    suspend fun getCountryList(
        params: LanguageCodeRequestBody,
    ): Flow<CountryListDataResponse> {
        return worldOperationRepository.getCountries(params)
    }

    suspend fun getTranslationData(params: TranslateOperationRequestBody): Flow<TranslationDataResponse> {
        return worldOperationRepository.getTranslationData(params)
    }

    suspend fun getTranslatedMessage(messageId: Long): Flow<TranslateMessageResponse> {
        return worldOperationRepository.getTranslatedMessageV2(messageId)
    }
    /*
        suspend fun getTranslatedMessage(messageId: Long): Flow<String> {
            return worldOperationRepository.getTranslatedMessage(messageId)
        }

     */

    suspend fun getMessageTranslationInformation(): Flow<String> {
        return worldOperationRepository.getMessageTranslationInformation()
    }
}
