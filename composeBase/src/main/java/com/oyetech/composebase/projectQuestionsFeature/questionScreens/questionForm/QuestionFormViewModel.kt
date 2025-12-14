package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.helpers.listOperations.ListOperationDelegate
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionEventHandlerUseCase
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.questionAnswerOverlayFlow
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.domain.useCases.AnswerUseCase
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Erdi Özbek
 * -17.11.2025-
 *
 * ViewModel for Question Form Screen
 */
class QuestionFormViewModel(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val answerUseCase: AnswerUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val questionSupabaseRepository: QuestionSupabaseRepository,
) : BaseViewModel(appDispatchers) {

    private val questionFormListOperationDelegate: ListOperationDelegate<QuestionViewUiState> =
        ListOperationDelegate(
            scope = viewModelScope,
            dispatcher = appDispatchers.io,
            initialDataFlow = flow {
                uiState.collect {
                    if (it.questions.isNotEmpty()) {
                        emit(it.questions)
                    }
                }
            },
            loadMoreFlow = MutableStateFlow(emptyList()),
            keySelector = { it.questionId }
        )

    private val questionEventHandlerUseCase = QuestionEventHandlerUseCase(this.viewModelScope)

    val listUiState: StateFlow<GenericListState<QuestionViewUiState>> =
        questionFormListOperationDelegate.listUiState

    private val _uiState = MutableStateFlow(QuestionFormScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<QuestionFormUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            questionFormListOperationDelegate.listUiState.map { it.items }
                .distinctUntilChanged().filter { it.isNotEmpty() }
                .questionAnswerOverlayFlow(answerUseCase.answersState)
                .collectLatest { questions ->
                    Timber.d("Combining question items flow with transformed questions: ${questions.size}")
                    observeQuestionListChanges(questions)
                    if (questions.isNotEmpty()) {
                        questionFormListOperationDelegate.updateList(questions)
                    }
                }
        }
    }

    fun observeQuestionListChanges(questions: List<QuestionViewUiState>) {
        val isAllAnswered = questions.all { it.isAnsweredByUser }
        _uiState.update { currentState ->
            currentState.copy(
                canSubmit = isAllAnswered && !currentState.isLocked
            )
        }

        val answeredCount = questions.count { it.isAnsweredByUser }
        _uiState.update { currentState ->
            currentState.copy(
                answeredCount = answeredCount,
            )
        }
        val totalCount = questions.count()
        _uiState.update { currentState ->
            currentState.copy(
                totalCount = totalCount,
            )
        }
    }

    fun getFormDetail(formId: String, userId: String) {
        viewModelScope.launch(getDispatcherIo()) {
            _uiState.update { it.copy(isLoading = true, isError = false) }

            questionSupabaseRepository.getCatalogDetail(formId, userId)
                .collectLatest { response ->
                    val questionItems = response.questions.map { question ->
                        question.toQuestionViewUiStateForForm()
                    }.toImmutableList()

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            formId = response.formId,
                            title = response.title,
                            description = response.description,
                            questions = questionItems,
                            canSubmit = false
                        )
                    }
                }
        }
    }

    /**
     * Handle events
     */
    fun onEvent(event: QuestionFormEvent) {
        when (event) {
            is QuestionFormEvent.OnFormLoad -> {
                val userId = firebaseUserRepository.getUserId()
                getFormDetail(event.formId, userId)
            }

            is QuestionFormEvent.OnSubmitForm -> handleSubmitForm()
            is QuestionFormEvent.OnEditForm -> handleEditForm()
            is QuestionFormEvent.OnCancelEdit -> handleCancelEdit()
            is QuestionFormEvent.OnBackPressed -> handleBackPressed()

            is QuestionFormEvent.OnQuestionExpanded -> handleQuestionExpanded(
                event.questionId,
                event.isExpanded
            )

            is QuestionFormEvent.OnClearAnswer -> handleClearAnswer(event.questionId)
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
            delay(1000)

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    isSubmitted = true,
                    isLocked = true,
                    submittedAt = System.currentTimeMillis()
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
                    isLocked = false
                )
            }
            _uiEvent.emit(QuestionFormUiEvent.OnFormUnlocked)
        }
    }

    private fun handleCancelEdit() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLocked = true
                )
            }
            _uiEvent.emit(QuestionFormUiEvent.OnFormLocked)
        }
    }

    private fun handleBackPressed() {
        navigationUseCase.navigateTo("back")
    }

    private fun handleQuestionExpanded(questionId: String, isExpanded: Boolean) {
        // Note: QuestionViewUiState doesn't have isExpanded field
        // This can be handled in the UI layer if needed
    }

    private fun handleClearAnswer(questionId: String) {
        _uiState.update { state ->
            val updatedQuestions = state.questions.map { question ->
                if (question.questionId == questionId) {
                    question.copy(
                        selectedAnswer = null,
                        isAnsweredByUser = false
                    )
                } else {
                    question
                }
            }.toImmutableList()

            val allAnswered = updatedQuestions.all { it.isAnsweredByUser }

            state.copy(
                questions = updatedQuestions,
                canSubmit = allAnswered && !state.isLocked
            )
        }
    }

    private fun handleErrorDismiss() {
        _uiState.update { it.copy(isError = false, errorText = "") }
    }

    fun onQuestionEvent(it: QuestionViewEvent) {
        // todo will change with locked variable.
        if (_uiState.value.canSubmit) {
            Timber.d("Form is locked, ignoring question event")
            return
        }


        questionEventHandlerUseCase.handleQuestionEvent(
            event = it,
            listOperationDelegate =
                questionFormListOperationDelegate
        )
    }
}
