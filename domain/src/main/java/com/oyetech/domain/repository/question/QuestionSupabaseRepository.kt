package com.oyetech.domain.repository.question

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface QuestionSupabaseRepository {
    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody>
}
