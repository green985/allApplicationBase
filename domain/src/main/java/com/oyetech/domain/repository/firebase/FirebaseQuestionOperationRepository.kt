package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionOperationRepository {
    // Creates a question in Firestore (transactional). Emits Unit on success; throws on failure.
    @Suppress("TooGenericExceptionThrown")
    suspend fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit>
}
