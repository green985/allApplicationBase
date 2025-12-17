package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QueAnswer(
    val questionId: String = "",
    val formId: String? = null,
    val type: QuestionType = QuestionType.SINGLE_CHOICE,
    val selectedOptionIds: List<String>? = null,
    val numericValue: Double? = null,
    val textValue: String? = null,
    val userId: String = "",
    val createdAt: String = "",
)
