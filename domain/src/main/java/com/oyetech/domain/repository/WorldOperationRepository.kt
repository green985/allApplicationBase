package com.oyetech.domain.repository

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
-19.03.2022-
-16:02-
 **/

interface WorldOperationRepository {
    suspend fun getApplicationTextResources(
        params: LanguageCodeRequestBody,
    ): Flow<TextResourcesDataResponse>

    suspend fun getLanguages(params: LanguageCodeRequestBody): Flow<LanguagesListDataResponse>

    suspend fun getCountries(params: LanguageCodeRequestBody): Flow<CountryListDataResponse>
    suspend fun getTranslationData(params: TranslateOperationRequestBody): Flow<TranslationDataResponse>
    fun getTranslatedMessage(messageId: Long): Flow<String>
    fun getMessageTranslationInformation(): Flow<String>
    fun getTranslatedMessageV2(messageId: Long): Flow<TranslateMessageResponse>
}
