package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Erdi Özbek
 * -17.11.2025-
 *
 * ViewModel for Question Form Screen
 */
class QuestionFormViewModel(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    private val _uiState = MutableStateFlow(QuestionFormScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<QuestionFormUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    /**
     * Initialize form with data
     */
    fun initializeForm(
        formId: String,
        title: String,
        description: String,
        questions: List<QuestionOperationResponseBody>,
    ) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val questionItems = questions.map { question ->
                    question.toQuestionItemState()
                }.toImmutableList()

                currentState.copy(
                    isLoading = false,
                    formId = formId,
                    title = title,
                    description = description,
                    questions = questionItems,
                    canSubmit = false
                )
            }
        }
    }

    /**
     * Handle events
     */
    fun onEvent(event: QuestionFormEvent) {
        when (event) {
            is QuestionFormEvent.OnSubmitForm -> handleSubmitForm()
            is QuestionFormEvent.OnEditForm -> handleEditForm()
            is QuestionFormEvent.OnCancelEdit -> handleCancelEdit()
            is QuestionFormEvent.OnBackPressed -> handleBackPressed()
            is QuestionFormEvent.OnQuestionAnswered -> handleQuestionAnswered(
                event.questionId,
                event.optionId
            )

            is QuestionFormEvent.OnQuestionExpanded -> handleQuestionExpanded(
                event.questionId,
                event.isExpanded
            )

            is QuestionFormEvent.OnClearAnswer -> handleClearAnswer(event.questionId)
            is QuestionFormEvent.OnCommentTextChanged -> handleCommentTextChanged(event.text)
            is QuestionFormEvent.OnAddComment -> handleAddComment()
            is QuestionFormEvent.OnDeleteComment -> handleDeleteComment(event.commentId)
            is QuestionFormEvent.OnErrorDismiss -> handleErrorDismiss()
        }
    }

    private fun handleSubmitForm() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validate
            if (!currentState.allQuestionsAnswered()) {
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorText = "Please answer all questions before submitting"
                    )
                }
                return@launch
            }

            // Simulate submit
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            // TODO: Call repository to submit answers
            // For now, simulate success
            kotlinx.coroutines.delay(1000)

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    isSubmitted = true,
                    isLocked = true,
                    submittedAt = System.currentTimeMillis(),
                    questions = state.questions.map { question ->
                        question.copy(isEnabled = false)
                    }.toImmutableList()
                )
            }

            _uiEvent.emit(QuestionFormUiEvent.OnSubmitSuccess)
            _uiEvent.emit(QuestionFormUiEvent.OnFormLocked)
        }
    }

    private fun handleEditForm() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLocked = false,
                    questions = state.questions.map { question ->
                        question.copy(isEnabled = true)
                    }.toImmutableList()
                )
            }
            _uiEvent.emit(QuestionFormUiEvent.OnFormUnlocked)
        }
    }

    private fun handleCancelEdit() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLocked = true,
                    questions = state.questions.map { question ->
                        question.copy(isEnabled = false)
                    }.toImmutableList()
                )
            }
            _uiEvent.emit(QuestionFormUiEvent.OnFormLocked)
        }
    }

    private fun handleBackPressed() {
        viewModelScope.launch {
            _uiEvent.emit(QuestionFormUiEvent.OnNavigateBack())
        }
    }

    private fun handleQuestionAnswered(questionId: String, optionId: String) {
        _uiState.update { state ->
            val updatedQuestions = state.questions.map { question ->
                if (question.questionId == questionId) {
                    question.copy(
                        selectedOptionId = optionId,
                        isAnswered = true
                    )
                } else {
                    question
                }
            }.toImmutableList()

            val allAnswered = updatedQuestions.all { it.isAnswered }

            state.copy(
                questions = updatedQuestions,
                canSubmit = allAnswered && !state.isLocked
            )
        }
    }

    private fun handleQuestionExpanded(questionId: String, isExpanded: Boolean) {
        _uiState.update { state ->
            val updatedQuestions = state.questions.map { question ->
                if (question.questionId == questionId) {
                    question.copy(isExpanded = isExpanded)
                } else {
                    question
                }
            }.toImmutableList()

            state.copy(questions = updatedQuestions)
        }
    }

    private fun handleClearAnswer(questionId: String) {
        _uiState.update { state ->
            val updatedQuestions = state.questions.map { question ->
                if (question.questionId == questionId) {
                    question.copy(
                        selectedOptionId = null,
                        isAnswered = false
                    )
                } else {
                    question
                }
            }.toImmutableList()

            val allAnswered = updatedQuestions.all { it.isAnswered }

            state.copy(
                questions = updatedQuestions,
                canSubmit = allAnswered && !state.isLocked
            )
        }
    }

    private fun handleCommentTextChanged(text: String) {
        _uiState.update { it.copy(commentInputText = text) }
    }

    private fun handleAddComment() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.commentInputText.isBlank()) return@launch

            // TODO: Call repository to add comment
            // For now, add locally
            val newComment = FormComment(
                commentId = "comment_${System.currentTimeMillis()}",
                userId = "current_user",
                userName = "Current User",
                commentText = currentState.commentInputText,
                createdAt = System.currentTimeMillis()
            )

            _uiState.update { state ->
                state.copy(
                    comments = (state.comments + newComment).toImmutableList(),
                    commentInputText = ""
                )
            }
        }
    }

    private fun handleDeleteComment(commentId: String) {
        viewModelScope.launch {
            // TODO: Call repository to delete comment
            _uiState.update { state ->
                state.copy(
                    comments = state.comments.filterNot { it.commentId == commentId }
                        .toImmutableList()
                )
            }
        }
    }

    private fun handleErrorDismiss() {
        _uiState.update { it.copy(isError = false, errorText = "") }
    }
}

