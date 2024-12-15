package com.oyetech.domain.useCases

import com.oyetech.domain.repository.GeneralOperationRepository
import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-16.07.2022-
-17:14-
 **/

class GeneralOperationUseCase(private var generalOperationRepository: GeneralOperationRepository) {

    fun sendFeedback(feedbackOperationRequestBody: FeedbackOperationRequestBody): Flow<Boolean> {
        return generalOperationRepository.sendFeedback(feedbackOperationRequestBody)
    }
}
