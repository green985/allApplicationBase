package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QuestionFilterParam(
    val moderationStatus: ModerationStatus? = null,
    val tags: List<String>? = null,
    val questionType: QuestionType? = null,
    val createdBy: String? = null,
    val limit: Int? = 20,
    val offset: Int? = 0,
)

