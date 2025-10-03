package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState

// Minimal UI state (toolbar, etc.)
data class QuestionListUiState(
    val toolbarTitleText: String = "Questions",
)

sealed class QuestionListEvent : BaseEvent() {
    data object OnRefreshClicked : QuestionListEvent()
    data class OnItemClicked(val item: QuestionViewUiState) : QuestionListEvent()
}
