package com.oyetech.models.questionProject.questionOperation

data class SubmitCatalogRequest(
    val formId: String,
    val userId: String,
    val questions: List<QuestionOperationResponseBody>,
)

data class ChatGptResult(
    val resultText: String,
    val note: String,
    val generatedAt: String,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val notificationSent: Boolean,
)

data class SubmitCatalogResponse(
    val formId: String,
    val userId: String,
    val chatGptResult: ChatGptResult,
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
