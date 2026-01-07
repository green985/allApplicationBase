package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.questionFormList

import com.oyetech.models.questionProject.questionOperation.CatalogItem

data class CatalogItemUiState(
    val formId: String,
    val title: String,
    val isCompleted: Boolean,
)

fun CatalogItem.toUiState(): CatalogItemUiState {
    return CatalogItemUiState(
        formId = this.formId,
        title = this.title,
        isCompleted = this.isCompleted
    )
}
