package com.oyetech.models.questionProject.questionOperation

import com.oyetech.models.utils.moshi.deserialize

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
    val questionFormResultText: String,
    val questions: List<QuestionOperationResponseBody>,
)

fun QuestionFormDetailResponse.parsedChatGptResult(): ChatGptResult? =
    questionFormResultText.deserialize<ChatGptResult>()

