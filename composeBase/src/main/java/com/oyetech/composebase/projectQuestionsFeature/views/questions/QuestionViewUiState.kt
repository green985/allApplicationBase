package com.oyetech.composebase.projectQuestionsFeature.views.questions

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.models.questionProject.questionOperation.QueOption
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionCategories
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionOptionCatalog
import com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyDefaults
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.models.questionProject.questionOperation.categoryFromKey
import com.oyetech.models.questionProject.questionOperation.inferCategory
import com.oyetech.models.questionProject.questionOperation.threeChoiceSubFromKey
import com.oyetech.models.questionProject.questionOperation.twoChoiceSubFromKey
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
    val questionType: QuestionType = QuestionType.SINGLE_CHOICE,
    val options: ImmutableList<QueOption> = QuestionOptionCatalog.TwoChoice.YES_NO.toImmutableList(),
    val category: QuestionCategories? = options.inferCategory(),
    val selectedAnswer: String? = null,
    val selectedTags: ImmutableList<QueTag> = kotlinx.collections.immutable.persistentListOf(),

    // Flags
    val isAnsweredByUser: Boolean = false,
    val isQuestionApproved: Boolean = false,
    val questionApproveView: Boolean = false,
    val questionApproveViewClicked: Boolean = false,
) : BaseUIState()

sealed class QuestionViewEvent : BaseEvent() {

    data class TitleChanged(val value: String) : QuestionViewEvent()
    object SubmitClicked : QuestionViewEvent()
    object CancelClicked : QuestionViewEvent()
    object OnErrorDismiss : QuestionViewEvent()

    // Extended selection with questionId
    data class OnOptionSelected(val questionId: String, val optionId: String) : QuestionViewEvent()

    // Clear existing answer for a question
    data class OnDeleteAnswerClicked(val questionId: String) : QuestionViewEvent()

    // Moderation actions
    data class OnAcceptClicked(val questionId: String) : QuestionViewEvent()
    data class OnDeclineClicked(val questionId: String) : QuestionViewEvent()
    data class OnEditClicked(val questionId: String) : QuestionViewEvent()

    // Tag actions
    data class OnTagSelected(val tag: QueTag) : QuestionViewEvent()
    data class OnTagSelectedForCreateQuestion(val tag: QueTag) : QuestionViewEvent()
    data class OnTagRemoved(val tag: QueTag) : QuestionViewEvent()
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
    // If backend provided options, use them; otherwise derive defaults from taxonomy or fallback to YES/NO
    val derivedOptions: List<QueOption> = this.options.ifEmpty {
        val category = categoryFromKey(this.taxonomy.categoryKey)
        when (category) {
            QuestionCategories.TWO_CHOICE -> {
                val sub = twoChoiceSubFromKey(this.taxonomy.subCategoryKey)
                sub?.let {
                    QuestionTaxonomyDefaults.defaultOptions(
                        QuestionCategories.TWO_CHOICE,
                        it
                    )
                }
                    ?: QuestionOptionCatalog.TwoChoice.YES_NO
            }

            QuestionCategories.THREE_CHOICE -> {
                val sub = threeChoiceSubFromKey(this.taxonomy.subCategoryKey)
                // For now, only YES/NO is supported to render; keep empty for non-two-choice
                sub?.let { emptyList() } ?: emptyList()
            }

            else -> {
                // Fallback to YES/NO
                QuestionOptionCatalog.TwoChoice.YES_NO
            }
        }
    }

    return base.copy(
        isLoading = false,
        isError = false,
        errorText = "",
        questionId = this.questionId,
        titleText = this.questionTitle,
        questionType = this.questionType,
        options = derivedOptions.toImmutableList(),
        isAnsweredByUser = false,
        isQuestionApproved = this.isQuestionApproved,
        selectedAnswer = null,
        selectedTags = this.tags.toImmutableList()
    )
}
