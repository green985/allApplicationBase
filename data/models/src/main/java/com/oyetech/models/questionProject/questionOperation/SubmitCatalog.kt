package com.oyetech.models.questionProject.questionOperation

data class SubmitCatalogRequest(
    val formId: String,
    val userId: String,
    val questions: List<QuestionOperationResponseBody>,
)

data class SubmitCatalogResponse(
    val resultText: String,
)

data class GenerateFormResultRequest(
    val formId: String,
    val userId: String,
    val prompt: String,
    val notificationToken: String? = null,
    val token: String? = null,
)

data class GenerateFormResultResponse(
    val resultText: String,
)
