package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.questionFormList

import com.oyetech.models.questionProject.questionOperation.CatalogItem

data class CatalogItemUiState(
    val formId: String,
    val title: String,
    val totalQuestions: Int,
    val answeredQuestionCount: Int = 0,
    val isCompleted: Boolean,
)

fun CatalogItem.toUiState(answeredQuestionCount: Int = 0): CatalogItemUiState {
    return CatalogItemUiState(
        formId = this.formId,
        title = this.title,
        answeredQuestionCount = answeredQuestionCount,
        totalQuestions = this.totalQuestions,
        isCompleted = this.isCompleted
    )
}
