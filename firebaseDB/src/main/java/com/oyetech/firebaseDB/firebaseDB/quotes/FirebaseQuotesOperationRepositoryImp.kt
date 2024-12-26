package com.oyetech.firebaseDB.firebaseDB.quotes

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
Created by Erdi Özbek
-25.12.2024-
-00:55-
 **/

class FirebaseQuotesOperationRepositoryImp(private val firestore: FirebaseFirestore) :
    FirebaseQuotesOperationRepository {

    override val saveListOperationState = MutableStateFlow<Boolean?>(null)

    override fun saveListWithNoTag(list: List<QuoteResponseData>) {
        // todo will be changed, maybe...
        GlobalScope.launch {
            list.map {
                firestore.collection(FirebaseDatabaseKeys.quotesNoTagCollection).add(it).await()
            }
        }
    }
}