package com.oyetech.composebase.projectQuestionsFeature.views.questions

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.models.questionProject.questionOperation.QueOption
import com.oyetech.models.questionProject.questionOperation.QueOptionsValues
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
    val questionType: QuestionType = QuestionType.YES_NO_QUESTION,
    val options: ImmutableList<QueOption> = QueOptionsValues.queYesNoQuestionOptionList.toImmutableList(),
    val isAnswered: Boolean = false,
    val selectedAnswer: String? = null,
) : BaseUIState()

sealed class QuestionViewEvent : BaseEvent() {

    data class TitleChanged(val value: String) : QuestionViewEvent()
    object SubmitClicked : QuestionViewEvent()
    object CancelClicked : QuestionViewEvent()
    object OnErrorDismiss : QuestionViewEvent()

    data class OnOptionSelected(val questionId: String, val optionId: String) : QuestionViewEvent()
}

// UI -> Backend model
fun QuestionViewUiState.toOperationBody(): QuestionOperationResponseBody {

    return QuestionOperationResponseBody(
        questionId = questionId,
        questionTitle = titleText,
        questionType = this.questionType,
        options = this.options,
        constraints = null,
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
        questionType = this.questionType,
        options = this.options.toImmutableList(),
        isAnswered = false,
        selectedAnswer = null,
    )
}
