package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Transactional Firestore operations for Questions.
 */
class FirebaseQuestionOperationRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseQuestionOperationRepository {

    @Suppress("TooGenericExceptionThrown")
    override suspend fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit> = flow {
        try {
            Timber.d("Creating question with ID: ${body.questionId}")
            val documentReference = firestore.runTransactionWithTimeout { transaction ->
                val collection = firestore.collection(FirebaseDatabaseKeys.createQuestion)
                val docRef =
                    if (body.questionId.isBlank()) collection.document() else collection.document(
                        body.questionId
                    )
                transaction.set(docRef, body)
                docRef
            }
            Timber.d("Question created: ${documentReference.id}")
            emit(Unit)
        } catch (e: Exception) {
            throw GeneralException(e.message ?: "Question create error")
        }
    }
}
