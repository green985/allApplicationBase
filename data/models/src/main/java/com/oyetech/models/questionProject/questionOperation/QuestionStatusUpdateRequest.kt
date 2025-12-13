package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QuestionStatusUpdateRequest(
    val questionId: String,
    val moderationStatus: ModerationStatus,
)
