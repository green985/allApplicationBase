package com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases

import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.helpers.listOperations.CreatedAtBasedPagingHandler
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

class GetUserAnsweredQuestionsPagedUseCase(
    private val repository: FirebaseQuestionOperationRepository,
) {

    private var currentUserId: String? = null

    private val handler = object : CreatedAtBasedPagingHandler<QuestionOperationResponseBody>(
        GeneralSettings.listSize()
    ) {

        override suspend fun fetchDataAfter(
            lastCreatedAtCursorMs: Long?,
            limit: Int,
        ): List<QuestionOperationResponseBody>? {
//            val uid = currentUserId.orEmpty()
//            if (uid.isBlank()) return Pair(emptyList(), null)
//            return repository.getUserAnsweredQuestionsPage(
//                userId = uid,
//                afterSubmittedAtMs = lastSubmittedAtCursorMs,
//                limit = limit
//            ).first
            return emptyList<QuestionOperationResponseBody>()
        }

        override fun extractCreatedAtEpochMs(item: QuestionOperationResponseBody): Long {
            return item.createdAt?.time ?: 0L
        }
    }

    fun updateUser(userId: String?) {
        currentUserId = userId
        handler.resetAll()
    }

    operator fun invoke(isInitial: Boolean): Flow<List<QuestionOperationResponseBody>> {
        return handler.getDataFlow(isInitial = isInitial)
    }
}