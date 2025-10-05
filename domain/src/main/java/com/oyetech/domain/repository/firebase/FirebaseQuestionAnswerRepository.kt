package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.QueAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FirebaseQuestionAnswerRepository {
    fun getAnswersByUser(userId: String): Flow<List<QueAnswer>>

    val answersState: StateFlow<List<QueAnswer>>

    fun submitAnswer(answer: QueAnswer): Flow<Unit>
    fun updateAnswer(answer: QueAnswer): Flow<Unit>
    fun deleteAnswer(userId: String, questionId: String): Flow<Unit>
}