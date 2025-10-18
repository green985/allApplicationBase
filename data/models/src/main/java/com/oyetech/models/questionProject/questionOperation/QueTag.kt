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

    val KNOWLEDGE = QueTag(
        id = "knowledge",
        name = "Knowledge & Understanding",
        color = "#2196F3",
        order = 2
    )

    val OPINION = QueTag(
        id = "opinion",
        name = "Opinion & Belief",
        color = "#9C27B0",
        order = 3
    )

    val EMOTION = QueTag(
        id = "emotion",
        name = "Emotion & Experience",
        color = "#E91E63",
        order = 4
    )

    val BEHAVIOR = QueTag(
        id = "behavior",
        name = "Behavior & Choice",
        color = "#FF9800",
        order = 5
    )

    val CREATIVITY = QueTag(
        id = "creativity",
        name = "Creativity & Imagination",
        color = "#8BC34A",
        order = 6
    )

    val SOCIETY = QueTag(
        id = "society",
        name = "Society & Connection",
        color = "#607D8B",
        order = 7
    )

    val FUTURE = QueTag(
        id = "future",
        name = "Future & Uncertainty",
        color = "#795548",
        order = 8
    )

    val questionCategoryTag = listOf(YES_NO, GOOD_BAD)

    val questionMeaningList = listOf(
        KNOWLEDGE, OPINION, EMOTION, BEHAVIOR, CREATIVITY, SOCIETY, FUTURE
    )

    val questionStyle = listOf(
        "YES_NO",
        "MULTIPLE_CHOICE",
        "OPEN_ENDED",
        "RATING_SCALE",
        "TRUE_FALSE",
        "RANKING",
        "COMPARISON",
        "SCENARIO_BASED"
    )


    val createQuestionTagList = buildList {
        addAll(questionMeaningList)
        addAll(questionCategoryTag)
    }

}
