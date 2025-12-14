package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class DeleteAnswerRequest(
    val userId: String,
    val questionId: String,
)
