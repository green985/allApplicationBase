package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QuestionListWithFilterResponse(
    val questions: List<QuestionOperationResponseBody> = emptyList(),
    val count: Int = 0,
    val filters: QueFilter = QueFilter.DEFAULT,
)
