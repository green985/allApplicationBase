package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseQuestionAnswerRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseQuestionAnswerRepository {

    override fun getAnswersByUser(userId: String): Flow<List<QueAnswer>> = flow {
        try {
            val snapshot = firestore
                .collection(FirebaseDatabaseKeys.questionAnswers)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val items = snapshot.documents.mapNotNull { it.toObject(QueAnswer::class.java) }
            emit(items)
        } catch (e: Exception) {
            throw GeneralException(e.message ?: "User answers fetch error")
        }
    }
}