package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toUiState
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class QuestionListVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val repository: FirebaseQuestionOperationRepository,
) : BaseListViewModel<QuestionViewUiState>(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())

    override val listViewState: MutableStateFlow<GenericListState<QuestionViewUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = repository.getQuestionList().map { list ->
                    list.map { it.toUiState(base = QuestionViewUiState(isLoading = false)) }
                },
                refreshDataFlow = repository.getQuestionList().map { list ->
                    list.map { it.toUiState(base = QuestionViewUiState(isLoading = false)) }
                }
            )
        )

    override fun onEvent(event: Any) {
        if (event is QuestionListEvent) {
            when (event) {
                QuestionListEvent.OnRefreshClicked -> refreshList()
                is QuestionListEvent.OnYesClicked -> onAnswered(event.item, true)
                is QuestionListEvent.OnNoClicked -> onAnswered(event.item, false)
                is QuestionListEvent.OnItemClicked -> onItemClicked(event.item)
            }
        }
    }

    private fun onAnswered(item: QuestionViewUiState, isYes: Boolean) {
        // Use questionType for next operations when available from item
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

    fun onQuestionEvent(event: QuestionViewEvent) {

    }
}
