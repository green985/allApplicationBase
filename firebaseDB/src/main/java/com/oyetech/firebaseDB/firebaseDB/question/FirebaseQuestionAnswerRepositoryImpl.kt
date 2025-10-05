package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseQuestionAnswerRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseQuestionAnswerRepository {

    private val _answersState = MutableStateFlow<List<QueAnswer>>(emptyList())
    override val answersState: StateFlow<List<QueAnswer>> = _answersState

    private fun answerDocId(userId: String, questionId: String): String = "${userId}_${questionId}"

    override fun getAnswersByUser(userId: String): Flow<List<QueAnswer>> = flow {
        val snapshot = firestore.collection(FirebaseDatabaseKeys.questionAnswers)
            .whereEqualTo("userId", userId).get().await()
        val items = snapshot.documents.mapNotNull { it.toObject(QueAnswer::class.java) }
        _answersState.value = items
        emit(items)
    }

    override fun submitAnswer(answer: QueAnswer): Flow<Unit> = flow {
        val docId = answerDocId(answer.userId, answer.questionId)
        firestore.collection(FirebaseDatabaseKeys.questionAnswers).document(docId).set(answer)
            .await()
        val current = _answersState.value.toMutableList().apply {
            removeAll { it.questionId == answer.questionId && it.userId == answer.userId }
            add(answer)
        }
        _answersState.value = current
        emit(Unit)
    }

    override fun updateAnswer(answer: QueAnswer): Flow<Unit> = flow {
        val docId = answerDocId(answer.userId, answer.questionId)
        firestore.collection(FirebaseDatabaseKeys.questionAnswers).document(docId).set(answer)
            .await()
        val current = _answersState.value.toMutableList().apply {
            removeAll { it.questionId == answer.questionId && it.userId == answer.userId }
            add(answer)
        }
        _answersState.value = current
        emit(Unit)
    }

    override fun deleteAnswer(userId: String, questionId: String): Flow<Unit> = flow {
        val docId = answerDocId(userId, questionId)
        firestore.collection(FirebaseDatabaseKeys.questionAnswers).document(docId).delete().await()
        val current = _answersState.value.toMutableList().apply {
            removeAll { it.questionId == questionId && it.userId == userId }
        }
        _answersState.value = current
        emit(Unit)
    }
}