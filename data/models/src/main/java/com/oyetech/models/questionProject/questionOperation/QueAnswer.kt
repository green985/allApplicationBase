package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Keep
data class QueAnswer(
    val questionId: String = "",
    val type: QuestionType = QuestionType.SINGLE_CHOICE,
    val selectedOptionIds: List<String>? = null,
    val numericValue: Double? = null,
    val textValue: String? = null,
    val userId: String = "",
    @ServerTimestamp val submittedAt: Date? = null,
)