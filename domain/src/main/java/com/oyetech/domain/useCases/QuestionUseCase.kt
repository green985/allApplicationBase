package com.oyetech.domain.useCases

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import kotlinx.coroutines.flow.Flow

class QuestionUseCase(
    private val questionSupabaseRepository: QuestionSupabaseRepository,
) {
    fun getQuestionListWithFilterParam(filterParam: QueFilter): Flow<QuestionListWithFilterResponse> {
        return questionSupabaseRepository.getQuestionListWithFilterParam(filterParam)
    }

    fun updateQuestionStatus(request: QuestionStatusUpdateRequest): Flow<QuestionOperationResponseBody> {
        return questionSupabaseRepository.updateQuestionStatus(request)
    }

    fun submitAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return questionSupabaseRepository.addAnswer(answer)
    }
}
