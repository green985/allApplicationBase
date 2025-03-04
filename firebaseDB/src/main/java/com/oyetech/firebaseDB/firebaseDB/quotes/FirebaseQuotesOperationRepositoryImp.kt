package com.oyetech.firebaseDB.firebaseDB.quotes

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuotesDebugOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.quotes.responseModel.AdviceQuoteResponseData
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
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
    FirebaseQuotesOperationRepository, FirebaseQuotesDebugOperationRepository {

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

    @Suppress("TooGenericExceptionThrown")
    override fun approveAdviceQuote(documentId: String) = flow {
        val documentReference =
            firestore.runTransactionWithTimeout() { transaction ->
                val commentRef =
                    firestore.collection(FirebaseDatabaseKeys.adviceQuoteCollection)
                        .document(documentId)
                transaction.update(commentRef, "status", "Approved")
                commentRef
            }

        Timber.d("Quote added: ${documentReference.id}")
        emit(Unit)
    }

    override fun getAllAdviceQuoteList(): Flow<List<AdviceQuoteResponseData>> {
        return flow<List<AdviceQuoteResponseData>> {
            val result =
                firestore.collection(FirebaseDatabaseKeys.adviceQuoteCollection).get().await()

            val documentIdList = arrayListOf<String>()
            val adviceQuoteList = result.mapNotNull {
                documentIdList.add(it.id)
                it.toObject(AdviceQuoteResponseData::class.java)
            }

            documentIdList.mapIndexed { index, s ->
                adviceQuoteList[index].documentId = s
            }
            emit(adviceQuoteList)
        }

    }
}