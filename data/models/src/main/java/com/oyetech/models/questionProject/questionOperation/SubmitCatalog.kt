package com.oyetech.models.questionProject.questionOperation

data class SubmitCatalogRequest(
    val formId: String,
    val userId: String,
    val questions: List<QuestionOperationResponseBody>,
)

data class SubmitCatalogResponse(
    val resultText: String,
)
