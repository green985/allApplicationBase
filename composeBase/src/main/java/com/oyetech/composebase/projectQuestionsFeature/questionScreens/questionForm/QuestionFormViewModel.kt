package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionEventHandlerUseCase
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.questionAnswerOverlayFlow
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toOperationBody
import com.oyetech.domain.repository.firebase.FirebaseTokenOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.domain.useCases.AnswerUseCase
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toUiState as questionToUiState

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
    private val googleLoginRepository: GoogleLoginRepository,
    private val questionSupabaseRepository: QuestionSupabaseRepository,
    private val firebaseTokenOperationRepository: FirebaseTokenOperationRepository,
) : BaseViewModel(appDispatchers) {
    private val questionEventHandlerUseCase = QuestionEventHandlerUseCase(this.viewModelScope)

    val listUiState2 = MutableStateFlow(GenericListState<QuestionViewUiState>())

    private val _uiState = MutableStateFlow(QuestionFormScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<QuestionFormUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            listUiState2.map { it.items }
                .distinctUntilChanged().filter { it.isNotEmpty() }
                .questionAnswerOverlayFlow(answerUseCase.answersState)
                .collectLatest { questions ->
                    Timber.d("Combining question items flow with transformed questions: ${questions.size}")
                    observeQuestionListChanges(questions)
                    if (questions.isNotEmpty()) {
                        listUiState2.updateState {
                            copy(items = questions.toImmutableList())
                        }
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
                        question.questionToUiState()
                    }.toImmutableList()
                    listUiState2.updateState {
                        copy(items = questionItems)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            formId = response.formId,
                            title = response.title,
                            description = response.description,
                            questions = questionItems,
                            submitResultText = response.questionFormResultText,
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
        viewModelScope.launch(getDispatcherIo()) {
            _uiState.updateState {
                copy(
                    questions = listUiState2.value.items.toImmutableList()
                )
            }
            val currentState = _uiState.value

            if (!currentState.allQuestionsAnswered()) {
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorText = "Please answer all questions before submitting"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val userId = firebaseUserRepository.getUserId()
            val questionsResponse = currentState.questions.map { it.toOperationBody() }

            questionSupabaseRepository.submitCatalog(
                formId = currentState.formId,
                userId = userId,
                questions = questionsResponse
            ).asResult().collectLatest { response ->
                response.fold(
                    onSuccess = { resp ->
                        _uiState.update { state ->
                            state.copy(
                                submitResultText = resp.resultText,
                                isLoading = false,
                                isSubmitted = true,
                                isLocked = true,
                                submittedAt = System.currentTimeMillis()
                            )
                        }
                        _uiEvent.emit(QuestionFormUiEvent.OnSubmitSuccess)
                        _uiEvent.emit(QuestionFormUiEvent.OnFormLocked)

                        generateFormResult()
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error submitting form")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorText = error.message ?: "Unknown error occurred"
                            )
                        }
                        _uiEvent.emit(
                            QuestionFormUiEvent.OnSubmitError(
                                error.message ?: "Unknown error occurred"
                            )
                        )
                    }
                )
            }
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
//        if (_uiState.value.canSubmit) {
//            Timber.d("Form is locked, ignoring question event")
//            return
//        }


        questionEventHandlerUseCase.handleQuestionEvent(
            event = it,
            listUiState = listUiState2
        )
    }

    private fun generateFormResult() {
        viewModelScope.launch(getDispatcherIo()) {
            val currentState = _uiState.value
            val userId = firebaseUserRepository.getUserId()
            val notificationToken =
                firebaseTokenOperationRepository.firebaseTokenStateFlow.value?.notificationToken
            val prompt =
                "Verdiğiniz yanıtları analiz edip size özel bir değerlendirme hazırlıyorum. Bu süreç birkaç saniye sürebilir."

            _uiState.update { it.copy(isGeneratingResult = true) }
            val token = googleLoginRepository.googleUserStateFlow.value.token
            questionSupabaseRepository.generateFormResult(
                formId = currentState.formId,
                userId = userId,
                prompt = prompt,
                notificationToken = notificationToken,
                token = token,
            ).asResult().collectLatest { response ->
                response.fold(
                    onSuccess = { resp ->
                        _uiState.update { state ->
                            state.copy(
                                isGeneratingResult = false,
                                generatedResultText = resp.resultText ?: ""
                            )
                        }
                    },
                    onFailure = { error ->
                        Timber.e(error, "Error generating form result")
                        _uiState.update { state ->
                            state.copy(
                                isGeneratingResult = false,
                                generatedResultText = "Sonuç oluşturulurken bir hata oluştu."
                            )
                        }
                    }
                )
            }
        }
    }

}
