package com.oyetech.composebase.projectQuestionsFeature.views.questions

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

/**
Created by Erdi Özbek
-3.10.2025-
-01:08-
 **/

data class QuestionViewUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorText: String = "",
    val questionId: String = "",
    val titleText: String = "",
    val bodyText: String = "",
    val optionYesText: String = "",
    val optionNoText: String = "",
    val isAnswered: Boolean = false,
    val selectedAnswer: String? = null,
) : BaseUIState()

sealed class QuestionViewEvent : BaseEvent() {

    data class TitleChanged(val value: String) : QuestionViewEvent()
    data class OptionNoChanged(val value: String) : QuestionViewEvent()
    data class OptionYesChanged(val value: String) : QuestionViewEvent()
    object SubmitClicked : QuestionViewEvent()
    object CancelClicked : QuestionViewEvent()

    // Answering flow
    object RetryClicked : QuestionViewEvent()
    object YesClicked : QuestionViewEvent()
    object NoClicked : QuestionViewEvent()
    object DismissError : QuestionViewEvent()
}
