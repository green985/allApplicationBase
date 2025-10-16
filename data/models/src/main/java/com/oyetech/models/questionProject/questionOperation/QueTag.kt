package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QueTag(
    val id: String = "",
    val name: String = "",
    val color: String = "#000000",
    val order: Int = 0,
)

object QuestionTagCatalog {
    val YES_NO = QueTag(
        id = "yesNo",
        name = "Yes/No",
        color = "#4CAF50",
        order = 0
    )

    val GOOD_BAD = QueTag(
        id = "goodBad",
        name = "Good/Bad",
        color = "#FF5722",
        order = 1
    )

    val ALL = listOf(YES_NO, GOOD_BAD)
}
