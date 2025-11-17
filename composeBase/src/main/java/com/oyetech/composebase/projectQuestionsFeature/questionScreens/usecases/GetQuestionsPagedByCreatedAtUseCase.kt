package com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases

import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.helpers.listOperations.CreatedAtBasedPagingHandler
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

class GetQuestionsPagedByCreatedAtUseCase(
    private val repository: FirebaseQuestionOperationRepository,
) {

    private var currentModerationStatus: ModerationStatus? = null
    private var currentTag: QueTag? = null

    private val handler =
        object :
            CreatedAtBasedPagingHandler<QuestionOperationResponseBody>(GeneralSettings.listSize()) {
            override suspend fun fetchDataAfter(
                lastCreatedAtCursorMs: Long?,
                limit: Int,
            ): List<QuestionOperationResponseBody>? {
                return repository.getQuestionsFilteredPage(
                    moderationStatus = currentModerationStatus,
                    tag = currentTag,
                    afterCreatedAtMs = lastCreatedAtCursorMs,
                    limit = limit
                )
            }

            override fun extractCreatedAtEpochMs(item: QuestionOperationResponseBody): Long {
                return item.createdAt1 ?: 0L
            }
        }

    fun updateFilters(
        moderationStatus: ModerationStatus?,
        tag: QueTag?,
    ) {
        currentModerationStatus = moderationStatus
        currentTag = tag
        handler.resetAll()
    }

    operator fun invoke(
        isInitial: Boolean,
        moderationStatus: ModerationStatus? = currentModerationStatus,
        tag: QueTag? = currentTag,
    ): Flow<List<QuestionOperationResponseBody>> {
        currentModerationStatus = moderationStatus
        currentTag = tag
        return handler.getDataFlow(isInitial = isInitial)
    }
}