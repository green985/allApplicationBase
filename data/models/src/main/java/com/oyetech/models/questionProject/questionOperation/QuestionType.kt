package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
enum class QuestionType {
    YES_NO_QUESTION,
}

fun QuestionType.asString(): String = when (this) {
    QuestionType.YES_NO_QUESTION -> "YES_NO_QUESTION"
}

fun String.toQuestionTypeOrDefault(): QuestionType = when (this) {
    "YES_NO_QUESTION" -> QuestionType.YES_NO_QUESTION
    else -> QuestionType.YES_NO_QUESTION
}