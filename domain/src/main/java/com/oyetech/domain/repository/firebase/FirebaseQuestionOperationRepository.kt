package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionOperationRepository {
    // Creates a question in Firestore (transactional). Emits Unit on success; throws on failure.
    fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit>

    // Returns a list of questions. Emits once for now; can be extended to realtime updates.
    fun getQuestionList(): Flow<List<QuestionOperationResponseBody>>

    // Update moderation status for a question
    fun updateQuestionStatus(
        questionId: String,
        status: com.oyetech.models.questionProject.questionOperation.ModerationStatus,
    ): Flow<Unit>
}
