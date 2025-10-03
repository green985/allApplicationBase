package com.oyetech.domain.repository.firebase

import com.oyetech.models.newPackages.helpers.OperationState
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionOperationRepository {
    fun createQuestion(body: QuestionOperationResponseBody): Flow<OperationState<Boolean>>
}
