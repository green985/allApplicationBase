package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionOperationRepository {
    // Creates a question in Firestore (transactional). Emits Unit on success; throws on failure.
    fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit>

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

    // Get questions created by a specific user
    fun getUserQuestions(userId: String): Flow<List<QuestionOperationResponseBody>>

    // Get questions answered by a specific user
    fun getUserAnsweredQuestions(userId: String): Flow<List<QuestionOperationResponseBody>>
    suspend fun getQuestionsFilteredPage(
        moderationStatus: ModerationStatus?,
        tag: QueTag?,
        afterCreatedAtMs: Long?,
        limit: Int = 10,
    ): List<QuestionOperationResponseBody>?

    suspend fun getUserQuestionsPage(
        userId: String,
        afterCreatedAtMs: Long?,
        limit: Int,
    ): List<QuestionOperationResponseBody>

    suspend fun getUserAnsweredQuestionsPage(
        userId: String,
        afterSubmittedAtMs: Long?,
        limit: Int,
    ): Pair<List<QuestionOperationResponseBody>, Long?>
}
