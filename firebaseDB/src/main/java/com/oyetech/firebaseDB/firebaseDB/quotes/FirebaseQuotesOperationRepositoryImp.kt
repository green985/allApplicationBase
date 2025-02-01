package com.oyetech.firebaseDB.firebaseDB.quotes

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.quotes.responseModel.AdviceQuoteResponseData
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-25.12.2024-
-00:55-
 **/

class FirebaseQuotesOperationRepositoryImp(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) :
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

    @Suppress("TooGenericExceptionThrown")
    override suspend fun submitAdviceQuote(quote: AdviceQuoteResponseData) = flow {
        val username = userRepository.getUsername() ?: ""

        if (username.isBlank()) {
            throw Exception(LanguageKey.usernameIsEmpty)
        }

        val documentReference =
            firestore.runTransactionWithTimeout() { transaction ->
                val commentRef =
                    firestore.collection(FirebaseDatabaseKeys.adviceQuoteCollection)
                        .document()

                transaction.set(commentRef, quote)
                commentRef
            }

        Timber.d("Quote added: ${documentReference.id}")
        emit(Unit)
    }
}