package com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent

// UI State
data class QuestionCreateQuestionScreenUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val titleText: String = "",
    val descriptionText: String = "",
    val toolbarTitleText: String = "Create Question",
    val isSubmitEnabled: Boolean = false,
    val isSubmitted: Boolean = false,
)

// UI Events (one-shot)
sealed class QuestionCreateQuestionUiEvent : BaseUIEvent() {
    data object OnSubmitSuccess : QuestionCreateQuestionUiEvent()
    data class OnSubmitError(val message: String) : QuestionCreateQuestionUiEvent()
}

// View Events (from UI)
sealed class QuestionCreateQuestionEvent : BaseEvent() {
    data class OnTitleChange(val text: String) : QuestionCreateQuestionEvent()
    data object OnSubmit : QuestionCreateQuestionEvent()
    data object OnRetry : QuestionCreateQuestionEvent()
    data object OnScreenOut : QuestionCreateQuestionEvent()
}