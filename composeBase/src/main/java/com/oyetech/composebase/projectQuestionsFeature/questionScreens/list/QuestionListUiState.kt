package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState

// Minimal UI state (toolbar, etc.)
data class QuestionListUiState(
    val toolbarTitleText: String = "Questions",
    val currentFilter: QueFilter = QueFilter.DEFAULT,
)

sealed class QuestionListEvent : BaseEvent() {
    data object OnRefreshClicked : QuestionListEvent()
    data class OnItemClicked(val item: QuestionViewUiState) : QuestionListEvent()
    data class OnAdminFilterChanged(val filterType: QuestionListAdminFilterType) :
        QuestionListEvent()

    data class OnTagFilterChanged(
        val tag: com.oyetech.models.questionProject.questionOperation.QueTag? = null,
        val adminFilterType: QuestionListAdminFilterType? = null,
    ) :
        QuestionListEvent()
}
