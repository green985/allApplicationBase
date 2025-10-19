package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionOperationRepository {
    // Creates a question in Firestore (transactional). Emits Unit on success; throws on failure.
    fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit>

    // Returns a list of questions. Emits once for now; can be extended to realtime updates.
    fun getQuestionList(): Flow<List<QuestionOperationResponseBody>>

    // Filtered list by moderation status and/or tag. Defaults to APPROVED when moderationStatus is null.
    fun getQuestionsFiltered(
        moderationStatus: ModerationStatus? = ModerationStatus.APPROVED,
        tag: QueTag? = null,
    ): Flow<List<QuestionOperationResponseBody>>

    // Get a single question by ID
    fun getQuestionById(questionId: String): Flow<QuestionOperationResponseBody>

    // Update moderation status for a question
    fun updateQuestionStatus(
        questionId: String,
        status: ModerationStatus,
    ): Flow<Unit>

    // Update a question completely
    fun updateQuestion(body: QuestionOperationResponseBody): Flow<Unit>
}
