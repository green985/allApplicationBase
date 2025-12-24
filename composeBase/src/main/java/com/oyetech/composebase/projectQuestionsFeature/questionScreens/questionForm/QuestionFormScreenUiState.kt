package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Created by Erdi Özbek
 * -17.11.2025-
 *
 * Form/Catalog screen for displaying and answering multiple questions.
 *
 * Flow:
 * 1. User sees title and description
 * 2. User answers all questions in the catalog
 * 3. Submit button appears when all questions are answered
 * 4. After submit, questions become locked (read-only with answers visible)
 * 5. User can edit the form (unlocks questions again)
 */

data class QuestionFormScreenUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorText: String = "",

    // Form metadata
    val formId: String = "",
    val title: String = "",
    val description: String = "",

    val answeredCount: Int = 0,
    val totalCount: Int = 0,

    // Catalog list for horizontal list
    val catalogList: ImmutableList<CatalogItemUiState> = persistentListOf(),
    val isCatalogListLoading: Boolean = false,

    // Questions catalog - now using QuestionViewUiState
    val questions: ImmutableList<QuestionViewUiState> = persistentListOf(),

    // Form state
    val submitResultText: String = "",
    val isSubmitted: Boolean = false,
    val isLocked: Boolean = false, // Questions are locked after submit
    val submittedAt: Long? = null,

    // AI Result generation
    val isGeneratingResult: Boolean = false,
    val generatedResultText: String = "",

    // Validation
    val canSubmit: Boolean = false, // All questions answered
    val validationErrors: ImmutableList<String> = persistentListOf(),
) : BaseUIState()

/**
 * Events for the form screen
 */
sealed class QuestionFormEvent : BaseEvent() {
    // Form actions
    data class OnFormLoad(val formId: String) : QuestionFormEvent()
    data object OnSubmitForm : QuestionFormEvent()
    data object OnEditForm : QuestionFormEvent()
    data object OnCancelEdit : QuestionFormEvent()
    data object OnBackPressed : QuestionFormEvent()
    data object OnLoadCatalogList : QuestionFormEvent()
    data class OnCatalogItemClick(val formId: String) : QuestionFormEvent()

    // Question interactions

    data class OnQuestionExpanded(
        val questionId: String,
        val isExpanded: Boolean,
    ) : QuestionFormEvent()

    data class OnClearAnswer(val questionId: String) : QuestionFormEvent()

    // Error handling
    data object OnErrorDismiss : QuestionFormEvent()
}

/**
 * One-shot UI events
 */
sealed class QuestionFormUiEvent : BaseUIEvent() {
    data object OnSubmitSuccess : QuestionFormUiEvent()
    data class OnSubmitError(val message: String) : QuestionFormUiEvent()
    data object OnFormLocked : QuestionFormUiEvent()
    data object OnFormUnlocked : QuestionFormUiEvent()
    data class OnNavigateBack(val reason: String = "") : QuestionFormUiEvent()
}

// Extension functions for validation
fun QuestionFormScreenUiState.validateForm(): List<String> {
    val errors = mutableListOf<String>()

    if (title.isBlank()) {
        errors.add("Form title is required")
    }

    val unansweredQuestions = questions.filter { !it.isAnsweredByUser }
    if (unansweredQuestions.isNotEmpty()) {
        errors.add("${unansweredQuestions.size} question(s) not answered")
    }

    return errors
}

fun QuestionFormScreenUiState.allQuestionsAnswered(): Boolean {
    return questions.isNotEmpty() && questions.all { it.isAnsweredByUser }
}

fun QuestionFormScreenUiState.getAnsweredCount(): Int {
    return questions.count { it.isAnsweredByUser }
}

fun QuestionFormScreenUiState.getTotalQuestions(): Int {
    return questions.size
}

// Preview helpers
fun previewQuestionFormScreenUiState(
    isSubmitted: Boolean = false,
    questionsCount: Int = 3,
): QuestionFormScreenUiState {
    val sampleQuestions = (1..questionsCount).map { index ->
        QuestionViewUiState(
            isLoading = false,
            questionId = "question_$index",
            titleText = "Sample Question $index: Do you agree with statement $index?",
        )
    }.toImmutableList()

    return QuestionFormScreenUiState(
        isLoading = false,
        isError = false,
        formId = "form_sample_123",
        title = "Customer Satisfaction Survey 2025",
        description = "Please answer all questions honestly. This survey helps us improve our services. " +
                "Your feedback is valuable and will be reviewed by our team. " +
                "All responses are confidential and will be used only for internal analysis.",
        questions = sampleQuestions,
        isSubmitted = isSubmitted,
        isLocked = isSubmitted,
        submittedAt = if (isSubmitted) System.currentTimeMillis() - 7200000 else null,
        canSubmit = sampleQuestions.all { it.isAnsweredByUser },
        validationErrors = persistentListOf()
    )
}

/**
 * Preview state for loading
 */
fun previewLoadingState(): QuestionFormScreenUiState {
    return QuestionFormScreenUiState(
        isLoading = true,
        formId = "form_loading",
        title = "Loading...",
        description = "",
        questions = persistentListOf()
    )
}

/**
 * Preview state for error
 */
fun previewErrorState(): QuestionFormScreenUiState {
    return QuestionFormScreenUiState(
        isLoading = false,
        isError = true,
        errorText = "Failed to load form. Please check your connection and try again.",
        formId = "form_error",
        title = "",
        description = "",
        questions = persistentListOf()
    )
}

/**
 * Preview state for empty form
 */
fun previewEmptyFormState(): QuestionFormScreenUiState {
    return QuestionFormScreenUiState(
        isLoading = false,
        formId = "form_empty",
        title = "Empty Form",
        description = "This form has no questions yet.",
        questions = persistentListOf(),
        canSubmit = false
    )
}
