package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.questionFormList

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class QuestionFormListUiState(
    val catalogList: ImmutableList<CatalogItemUiState> = persistentListOf(),
    val isLoading: Boolean = false,
)

sealed class QuestionFormListEvent {
    data class OnCatalogItemClick(val formId: String) : QuestionFormListEvent()
    data object OnRefresh : QuestionFormListEvent()
}
