package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Writes QuestionOperationResponseBody to Firestore questions collection.
 * Emits true on success, throws on failure.
 */
class FirebaseQuestionOperationRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    fun createQuestion(body: QuestionOperationResponseBody): Flow<Boolean> = flow {
        val collection = firestore.collection(FirebaseDatabaseKeys.questions)
        val docRef =
            if (body.questionId.isBlank()) collection.document() else collection.document(body.questionId)
        docRef.set(body).await()
        emit(true)
    }
}