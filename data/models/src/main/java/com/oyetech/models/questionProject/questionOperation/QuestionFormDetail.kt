package com.oyetech.models.questionProject.questionOperation

data class QuestionFormDetailRequest(
    val formId: String,
    val userId: String,
)

data class QuestionFormDetailResponse(
    val formId: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String,
    val questions: List<QuestionOperationResponseBody>,
)
