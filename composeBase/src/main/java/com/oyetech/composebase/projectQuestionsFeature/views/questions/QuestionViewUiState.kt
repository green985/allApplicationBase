package com.oyetech.composebase.projectQuestionsFeature.views.questions

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.models.questionProject.questionOperation.QueConstraints
import com.oyetech.models.questionProject.questionOperation.QueOption
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionType

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
    object SubmitClicked : QuestionViewEvent()
    object CancelClicked : QuestionViewEvent()
    object OnErrorDismiss : QuestionViewEvent()

    object YesClicked : QuestionViewEvent()
    object NoClicked : QuestionViewEvent()
}

// UI -> Backend model
fun QuestionViewUiState.toOperationBody(): QuestionOperationResponseBody {
    // For YES/NO type, provide two options and a simple constraint
    val yesOption = QueOption(id = "YES", text = optionYesText.ifBlank { "Yes" }, order = 0)
    val noOption = QueOption(id = "NO", text = optionNoText.ifBlank { "No" }, order = 1)

    return QuestionOperationResponseBody(
        questionId = questionId,
        questionTitle = titleText,
        questionType = QuestionType.YES_NO_QUESTION,
        options = listOf(yesOption, noOption),
        constraints = QueConstraints(required = true, minSelections = 1, maxSelections = 1),
        // createdAt is @ServerTimestamp and set by backend; null here is fine
        createdAt = null,
    )
}

// Backend model -> UI
fun QuestionOperationResponseBody.toUiState(
    base: QuestionViewUiState = QuestionViewUiState(isLoading = false),
): QuestionViewUiState {
    return base.copy(
        isLoading = false,
        isError = false,
        errorText = "",
        questionId = this.questionId,
        titleText = this.questionTitle,
        // bodyText, options are not part of response, keep as-is
        isAnswered = false,
        selectedAnswer = null,
    )
}
