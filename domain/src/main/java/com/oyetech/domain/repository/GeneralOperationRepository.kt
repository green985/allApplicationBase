package com.oyetech.domain.repository

import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import kotlinx.coroutines.flow.Flow

interface GeneralOperationRepository {

    // fun getSettingsInfo(params: SettingsInfoRequestBody): Flow<SettingsInfoResponse>

    fun sendFeedback(feedbackOperationRequestBody: FeedbackOperationRequestBody): Flow<Boolean>
}
