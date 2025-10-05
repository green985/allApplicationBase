package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toUiState
import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

class QuestionListVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val repository: FirebaseQuestionOperationRepository,
    private val userRepository: FirebaseUserRepository,
    private val answerRepository: FirebaseQuestionAnswerRepository,
) : BaseListViewModel<QuestionViewUiState>(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())
    private val answersState =
        MutableStateFlow(emptyList<QueAnswer>())

    override val listViewState: MutableStateFlow<GenericListState<QuestionViewUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = combine(
                    repository.getQuestionList(),
                    answersState,
                ) { questions, answers ->
                    overlayAnswers(questions, answers)
                },
                refreshDataFlow = combine(
                    repository.getQuestionList(),
                    answersState,
                ) { questions, answers ->
                    overlayAnswers(questions, answers)
                }
            )
        )

    init {
        // Fetch user answers on start
        viewModelScope.launch(getDispatcherIo()) {
            val uid = userRepository.getUserId()
            if (uid.isNotBlank()) {
                answerRepository.getAnswersByUser(uid).collectLatest { list ->
                    answersState.value = list
                }
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is QuestionListEvent) {
            when (event) {
                QuestionListEvent.OnRefreshClicked -> refreshList()
                is QuestionListEvent.OnItemClicked -> onItemClicked(event.item)
            }
        }
    }

    private fun onOptionSelected(questionId: String, optionId: String) {
        // For YES/NO question, optionId will be "YES" or "NO"; future types can extend this logic
        when (QuestionType.YES_NO_QUESTION) {
            QuestionType.YES_NO_QUESTION -> {
                // Hook for future persistence or navigation if needed
            }
        }
    }

    private fun onItemClicked(item: QuestionViewUiState) {
        // Branch based on future questionType when added to UI state
        when (QuestionType.YES_NO_QUESTION) {
            QuestionType.YES_NO_QUESTION -> {
                // Example: navigate to detail screen if needed
            }
        }
    }

    private fun overlayAnswers(
        questions: List<QuestionOperationResponseBody>,
        answers: List<QueAnswer>,
    ): List<QuestionViewUiState> {
        val answerMap = answers.associateBy { it.questionId }
        return questions.map { q ->
            val ui = q.toUiState(base = QuestionViewUiState(isLoading = false))
            val ans = answerMap[q.questionId]
            if (ans != null) {
                val selected = ans.selectedOptionIds?.firstOrNull()
                ui.copy(isAnswered = selected != null, selectedAnswer = selected)
            } else ui
        }
    }

    fun onQuestionEvent(event: QuestionViewEvent) {
        when (event) {
            is QuestionViewEvent.OnOptionSelected -> {
                onOptionSelected(
                    event.questionId,
                    event.optionId
                )
                Timber.d("Option selected: ${event.optionId} for question ${event.questionId}")
            }

            else -> {}
        }
    }
}
