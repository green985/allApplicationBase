package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.QueAnswer
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionAnswerRepository {
    fun getAnswersByUser(userId: String): Flow<List<QueAnswer>>
}