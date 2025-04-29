package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.language.FirebaseLanguageResponseData
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.flow.Flow

interface FirebaseLanguageOperationRepository {
    suspend fun getApplicationTextResources(languageCodeRequestBody: LanguageCodeRequestBody)
            : Flow<List<FirebaseLanguageResponseData>>

}