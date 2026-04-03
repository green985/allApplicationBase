package com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases

import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.helpers.listOperations.CreatedAtBasedPagingHandler
import com.oyetech.domain.useCases.QuestionUseCase
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.toQuestionList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetQuestionsPagedByCreatedAtUseCase(
    private val questionUseCase: QuestionUseCase,
) {

    private var currentModerationStatus: ModerationStatus? = null
    private var currentTag: QueTag? = null
    private var currentQuestionListType: String? = null
    private var currentUserId: String? = null

    private val handler =
        object :
            CreatedAtBasedPagingHandler<QuestionOperationResponseBody>(GeneralSettings.listSize()) {
            override suspend fun fetchDataAfter(
                lastCreatedAtCursorMs: Long?,
                limit: Int,
            ): List<QuestionOperationResponseBody>? {
                return questionUseCase.getQuestionListWithFilterParam(
                    QueFilter(
                        adminFilterType = QuestionListAdminFilterType.APPROVED_ADMIN,
                        selectedTagFilter = currentTag,
                        questionListType = currentQuestionListType,
                        userId = currentUserId,
                    )
                ).first().toQuestionList()

//                return repository.getQuestionsFilteredPage(
//                    moderationStatus = currentModerationStatus,
//                    tag = currentTag,
//                    afterCreatedAtMs = lastCreatedAtCursorMs,
//                    limit = limit
//                )
            }

            override fun extractCreatedAtEpochMs(item: QuestionOperationResponseBody): Long {
                return item.creationTime ?: 0L
            }
        }

    fun updateFilters(
        moderationStatus: ModerationStatus?,
        tag: QueTag?,
        questionListType: String? = null,
        userId: String? = null,
    ) {
        currentModerationStatus = moderationStatus
        currentTag = tag
        currentQuestionListType = questionListType
        currentUserId = userId
        handler.resetAll()
    }

    operator fun invoke(
        isInitial: Boolean,
        moderationStatus: ModerationStatus? = currentModerationStatus,
        tag: QueTag? = currentTag,
        questionListType: String? = currentQuestionListType,
        userId: String? = currentUserId,
    ): Flow<List<QuestionOperationResponseBody>> {
        val filtersChanged =
            currentModerationStatus != moderationStatus ||
                currentTag != tag ||
                currentQuestionListType != questionListType ||
                currentUserId != userId
        currentModerationStatus = moderationStatus
        currentTag = tag
        currentQuestionListType = questionListType
        currentUserId = userId
        if (filtersChanged) {
            handler.resetAll()
        }
        return handler.getDataFlow(isInitial = isInitial)
    }
}
