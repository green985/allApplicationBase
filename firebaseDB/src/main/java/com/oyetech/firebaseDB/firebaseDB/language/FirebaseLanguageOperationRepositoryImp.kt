package com.oyetech.firebaseDB.firebaseDB.language

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseLanguageOperationRepository
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.language.FirebaseLanguageResponseData
import com.oyetech.models.firebaseModels.language.FirebaseLanguageResponseDataWrapper
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-18.01.2025-
-18:27-
 **/

class FirebaseLanguageOperationRepositoryImp(
    private val firestore: FirebaseFirestore,

    ) : FirebaseLanguageOperationRepository {

    override suspend fun getApplicationTextResources(languageCodeRequestBody: LanguageCodeRequestBody):
            Flow<List<FirebaseLanguageResponseData>> {
        val result = firestore.collection(FirebaseDatabaseKeys.languageTable)
            .document(languageCodeRequestBody.languageCode)
            .get().await()

        val wrapper = result.get("wrapper") as? HashMap<*, *>

        if (wrapper == null) {
            Timber.d("No language found")
            return flowOf(emptyList())
        }

        val wrapperResult = FirebaseLanguageResponseDataWrapper.firebaseDocumentToObject(wrapper)


        return flowOf(wrapperResult)

    }

}