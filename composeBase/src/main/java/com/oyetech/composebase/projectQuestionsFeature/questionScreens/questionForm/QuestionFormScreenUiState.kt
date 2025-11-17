package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
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
 * 5. Comments section appears below description
 * 6. User can edit the form (unlocks questions again)
 */

data class QuestionFormScreenUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorText: String = "",

    // Form metadata
    val formId: String = "",
    val title: String = "",
    val description: String = "",

    // Questions catalog
    val questions: ImmutableList<QuestionItemState> = persistentListOf(),

    // Form state
    val isSubmitted: Boolean = false,
    val isLocked: Boolean = false, // Questions are locked after submit
    val submittedAt: Long? = null,

    // Comments section (visible after submit)
    val comments: ImmutableList<FormComment> = persistentListOf(),
    val commentInputText: String = "",

    // Validation
    val canSubmit: Boolean = false, // All questions answered
    val validationErrors: ImmutableList<String> = persistentListOf(),
) : BaseUIState()

/**
 * Individual question state within the form
 */
data class QuestionItemState(
    val questionId: String = "",
    val questionTitle: String = "",
    val questionData: QuestionOperationResponseBody,

    // Answer state
    val selectedOptionId: String? = null,
    val isAnswered: Boolean = false,

    // UI state
    val isExpanded: Boolean = true,
    val isEnabled: Boolean = true, // False when locked
)

/**
 * Comment data for the form
 */
data class FormComment(
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val commentText: String = "",
    val createdAt: Long = 0L,
)

/**
 * Events for the form screen
 */
sealed class QuestionFormEvent : BaseEvent() {
    // Form actions
    data object OnSubmitForm : QuestionFormEvent()
    data object OnEditForm : QuestionFormEvent()
    data object OnCancelEdit : QuestionFormEvent()
    data object OnBackPressed : QuestionFormEvent()

    // Question interactions
    data class OnQuestionAnswered(
        val questionId: String,
        val optionId: String,
    ) : QuestionFormEvent()

    data class OnQuestionExpanded(
        val questionId: String,
        val isExpanded: Boolean,
    ) : QuestionFormEvent()

    data class OnClearAnswer(val questionId: String) : QuestionFormEvent()

    // Comment actions
    data class OnCommentTextChanged(val text: String) : QuestionFormEvent()
    data object OnAddComment : QuestionFormEvent()
    data class OnDeleteComment(val commentId: String) : QuestionFormEvent()

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

    val unansweredQuestions = questions.filter { !it.isAnswered }
    if (unansweredQuestions.isNotEmpty()) {
        errors.add("${unansweredQuestions.size} question(s) not answered")
    }

    return errors
}

fun QuestionFormScreenUiState.allQuestionsAnswered(): Boolean {
    return questions.isNotEmpty() && questions.all { it.isAnswered }
}

fun QuestionFormScreenUiState.getAnsweredCount(): Int {
    return questions.count { it.isAnswered }
}

fun QuestionFormScreenUiState.getTotalQuestions(): Int {
    return questions.size
}

// Conversion functions
fun QuestionOperationResponseBody.toQuestionItemState(
    selectedOptionId: String? = null,
    isLocked: Boolean = false,
): QuestionItemState {
    return QuestionItemState(
        questionId = this.questionId,
        questionTitle = this.questionTitle,
        questionData = this,
        selectedOptionId = selectedOptionId,
        isAnswered = selectedOptionId != null,
        isExpanded = true,
        isEnabled = !isLocked
    )
}

// Preview helpers
fun previewQuestionFormScreenUiState(
    isSubmitted: Boolean = false,
    questionsCount: Int = 3,
): QuestionFormScreenUiState {
    val sampleQuestions = (1..questionsCount).map { index ->
        QuestionItemState(
            questionId = "question_$index",
            questionTitle = "Sample Question $index: Do you agree with statement $index?",
            questionData = QuestionOperationResponseBody(
                questionId = "question_$index",
                questionTitle = "Sample Question $index: Do you agree with statement $index?",
            ),
            selectedOptionId = if (isSubmitted || index == 1) "option_yes" else null,
            isAnswered = isSubmitted || index == 1,
            isExpanded = true,
            isEnabled = !isSubmitted
        )
    }.toImmutableList()

    val sampleComments = if (isSubmitted) {
        listOf(
            FormComment(
                commentId = "comment_1",
                userId = "user_123",
                userName = "John Doe",
                commentText = "Great form! Very clear questions.",
                createdAt = System.currentTimeMillis() - 3600000
            ),
            FormComment(
                commentId = "comment_2",
                userId = "user_456",
                userName = "Jane Smith",
                commentText = "I found question 2 a bit confusing.",
                createdAt = System.currentTimeMillis() - 1800000
            )
        ).toImmutableList()
    } else {
        persistentListOf()
    }

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
        comments = sampleComments,
        commentInputText = "",
        canSubmit = sampleQuestions.all { it.isAnswered },
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

