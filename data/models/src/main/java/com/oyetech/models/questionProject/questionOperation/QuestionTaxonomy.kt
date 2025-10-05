package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
enum class QuestionType {
    SINGLE_CHOICE,
//    MULTI_CHOICE,
//    SCALE,
//    OPEN_ENDED,
}

@Keep
enum class QuestionCategories {
    TWO_CHOICE,
    THREE_CHOICE,
    MULTI_CHOICE,
    SCALE,
    OPEN_ENDED,
}

@Keep
enum class TwoChoiceSubCategories {
    YES_NO,
    UP_DOWN,
    GOOD_BAD,
}

@Keep
enum class ThreeChoiceSubCategories {
    LOW_MED_HIGH,
    AGREE_NEUTRAL_DISAGREE,
}

object QuestionCategoryKeys {
    const val TWO_CHOICE: String = "2choice"
    const val THREE_CHOICE: String = "3choice"
    const val MULTI_CHOICE: String = "multi_choice"
    const val SCALE: String = "scale"
    const val OPEN_ENDED: String = "open_ended"
}

object TwoChoiceSubCategoryKeys {
    const val YES_NO: String = "yes_no"
    const val UP_DOWN: String = "up_down"
    const val GOOD_BAD: String = "good_bad"
}

object ThreeChoiceSubCategoryKeys {
    const val LOW_MED_HIGH: String = "low_med_high"
    const val AGREE_NEUTRAL_DISAGREE: String = "agree_neutral_disagree"
}

fun QuestionCategories.asKey(): String = when (this) {
    QuestionCategories.TWO_CHOICE -> QuestionCategoryKeys.TWO_CHOICE
    QuestionCategories.THREE_CHOICE -> QuestionCategoryKeys.THREE_CHOICE
    QuestionCategories.MULTI_CHOICE -> QuestionCategoryKeys.MULTI_CHOICE
    QuestionCategories.SCALE -> QuestionCategoryKeys.SCALE
    QuestionCategories.OPEN_ENDED -> QuestionCategoryKeys.OPEN_ENDED
}

fun TwoChoiceSubCategories.asKey(): String = when (this) {
    TwoChoiceSubCategories.YES_NO -> TwoChoiceSubCategoryKeys.YES_NO
    TwoChoiceSubCategories.UP_DOWN -> TwoChoiceSubCategoryKeys.UP_DOWN
    TwoChoiceSubCategories.GOOD_BAD -> TwoChoiceSubCategoryKeys.GOOD_BAD
}

fun ThreeChoiceSubCategories.asKey(): String = when (this) {
    ThreeChoiceSubCategories.LOW_MED_HIGH -> ThreeChoiceSubCategoryKeys.LOW_MED_HIGH
    ThreeChoiceSubCategories.AGREE_NEUTRAL_DISAGREE -> ThreeChoiceSubCategoryKeys.AGREE_NEUTRAL_DISAGREE
}

/**
 * Catalog of reusable QueOption definitions and grouped templates per subcategory.
 */
object QuestionOptionCatalog {

    object TwoChoice {
        // Atomic options
        val YES = QueOption(id = "YES", text = "Yes", order = 0)
        val NO = QueOption(id = "NO", text = "No", order = 1)
        val UP = QueOption(id = "UP", text = "Up", order = 0)
        val DOWN = QueOption(id = "DOWN", text = "Down", order = 1)
        val GOOD = QueOption(id = "GOOD", text = "Good", order = 0)
        val BAD = QueOption(id = "BAD", text = "Bad", order = 1)

        // Grouped templates
        val YES_NO = listOf(YES, NO)
        val UP_DOWN = listOf(UP, DOWN)
        val GOOD_BAD = listOf(GOOD, BAD)
    }

    object ThreeChoice {
        // Atomic options
        val LOW = QueOption(id = "LOW", text = "Low", order = 0)
        val MEDIUM = QueOption(id = "MEDIUM", text = "Medium", order = 1)
        val HIGH = QueOption(id = "HIGH", text = "High", order = 2)

        val AGREE = QueOption(id = "AGREE", text = "Agree", order = 0)
        val NEUTRAL = QueOption(id = "NEUTRAL", text = "Neutral", order = 1)
        val DISAGREE = QueOption(id = "DISAGREE", text = "Disagree", order = 2)

        // Grouped templates
        val LOW_MED_HIGH = listOf(LOW, MEDIUM, HIGH)
        val AGREE_NEUTRAL_DISAGREE = listOf(AGREE, NEUTRAL, DISAGREE)
    }
}

// Helpers to map string keys to enums
fun categoryFromKey(key: String): QuestionCategories? = when (key) {
    QuestionCategoryKeys.TWO_CHOICE -> QuestionCategories.TWO_CHOICE
    QuestionCategoryKeys.THREE_CHOICE -> QuestionCategories.THREE_CHOICE
    QuestionCategoryKeys.MULTI_CHOICE -> QuestionCategories.MULTI_CHOICE
    QuestionCategoryKeys.SCALE -> QuestionCategories.SCALE
    QuestionCategoryKeys.OPEN_ENDED -> QuestionCategories.OPEN_ENDED
    else -> null
}

fun twoChoiceSubFromKey(key: String?): TwoChoiceSubCategories? = when (key) {
    TwoChoiceSubCategoryKeys.YES_NO -> TwoChoiceSubCategories.YES_NO
    TwoChoiceSubCategoryKeys.UP_DOWN -> TwoChoiceSubCategories.UP_DOWN
    TwoChoiceSubCategoryKeys.GOOD_BAD -> TwoChoiceSubCategories.GOOD_BAD
    else -> null
}

fun threeChoiceSubFromKey(key: String?): ThreeChoiceSubCategories? = when (key) {
    ThreeChoiceSubCategoryKeys.LOW_MED_HIGH -> ThreeChoiceSubCategories.LOW_MED_HIGH
    ThreeChoiceSubCategoryKeys.AGREE_NEUTRAL_DISAGREE -> ThreeChoiceSubCategories.AGREE_NEUTRAL_DISAGREE
    else -> null
}

object QuestionTaxonomyDefaults {
    fun defaultOptions(category: QuestionCategories, sub: TwoChoiceSubCategories): List<QueOption> {
        return when (category) {
            QuestionCategories.TWO_CHOICE -> when (sub) {
                TwoChoiceSubCategories.YES_NO -> QuestionOptionCatalog.TwoChoice.YES_NO
                TwoChoiceSubCategories.UP_DOWN -> QuestionOptionCatalog.TwoChoice.UP_DOWN
                TwoChoiceSubCategories.GOOD_BAD -> QuestionOptionCatalog.TwoChoice.GOOD_BAD
            }

            else -> emptyList()
        }
    }

    fun defaultOptions(
        category: QuestionCategories,
        sub: ThreeChoiceSubCategories,
    ): List<QueOption> {
        return when (category) {
            QuestionCategories.THREE_CHOICE -> when (sub) {
                ThreeChoiceSubCategories.LOW_MED_HIGH -> QuestionOptionCatalog.ThreeChoice.LOW_MED_HIGH
                ThreeChoiceSubCategories.AGREE_NEUTRAL_DISAGREE -> QuestionOptionCatalog.ThreeChoice.AGREE_NEUTRAL_DISAGREE
            }

            else -> emptyList()
        }
    }

    fun defaultConstraints(category: QuestionCategories): QueConstraints {
        return when (category) {
            QuestionCategories.TWO_CHOICE,
            QuestionCategories.THREE_CHOICE,
            QuestionCategories.MULTI_CHOICE,
                -> QueConstraints(required = true, minSelections = 1, maxSelections = 1)

            QuestionCategories.SCALE -> QueConstraints(
                required = true,
                minValue = 0.0,
                maxValue = 100.0,
                step = 1.0
            )

            QuestionCategories.OPEN_ENDED -> QueConstraints(required = false)
        }
    }
}