package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.helpers.listOperations.ListOperationDelegate
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases.GetQuestionsPagedByCreatedAtUseCase
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toUiState
import com.oyetech.domain.useCases.AnswerUseCase
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import com.oyetech.models.questionProject.questionOperation.QuestionListType
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.models.questionProject.questionOperation.toModerationStatusOrNull
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("TooManyFunctions", "LongParameterList")
class QuestionListVm(
    appDispatchers: AppDispatchers,
    private val answerUseCase: AnswerUseCase,
    private val getQuestionsPagedByCreatedAtUseCase: GetQuestionsPagedByCreatedAtUseCase,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())
    private val queFilter = MutableStateFlow<QueFilter?>(null)
    private val adminViewState = MutableStateFlow(false)
    private val adminFilterType = MutableStateFlow(QuestionListAdminFilterType.APPROVED_ADMIN)

    private val listOperationDelegate = ListOperationDelegate(
        scope = viewModelScope,
        dispatcher = getDispatcherIo(),
        initialDataFlow = getQuestionDataFlow(isInitial = true),
        loadMoreFlow = getQuestionDataFlow(isInitial = false),
        keySelector = { it.questionId }
    )

    val listViewState: StateFlow<GenericListState<QuestionViewUiState>> =
        listOperationDelegate.listUiState

    private val questionEventHandlerUseCase = QuestionEventHandlerUseCase(
        scope = viewModelScope,
        adminViewState = adminViewState,
        adminFilterType = adminFilterType
    )

    init {
        questionItemsFlow(listOperationDelegate).observeQuestionAnswerToList()
    }

    private fun Flow<List<QuestionViewUiState>>.observeQuestionAnswerToList() {
        viewModelScope.launch(getDispatcherIo()) {
            this@observeQuestionAnswerToList.filter { it.isNotEmpty() }
                .getQuestionTransformerFlow(answerUseCase.answersState)
                .collectLatest { questions ->
                    Timber.d("Combining question items flow with transformed questions: ${questions.size}")
                    if (questions.isNotEmpty()) {
                        listOperationDelegate.updateList(questions)
                    }
                }
        }
    }

    fun questionItemsFlow(
        listOperationDelegate: ListOperationDelegate<QuestionViewUiState>,
    ): Flow<List<QuestionViewUiState>> {
        return listOperationDelegate.listUiState
            .map { it.items }
            .distinctUntilChanged()
    }

    private fun Flow<List<QuestionViewUiState>>.getQuestionTransformerFlow(answersState: StateFlow<List<QueAnswer>>): Flow<List<QuestionViewUiState>> {
        return questionAnswerOverlayFlow(answersState).combine(adminViewState) { qs, isAdminView ->
            if (qs.isEmpty()) return@combine qs
            qs.map { if (it.isAdminView != isAdminView) it.copy(isAdminView = isAdminView) else it }
        }.combine(adminFilterType) { qs, filterType ->
            if (qs.isEmpty()) return@combine qs
            qs.map { if (it.adminFilterType != filterType) it.copy(adminFilterType = filterType) else it }
        }
    }

    fun getQuestionDataFlow(isInitial: Boolean): Flow<List<QuestionViewUiState>> {
        return queFilter.filterNotNull().flatMapLatest { filter ->
            Timber.d(
                "Filter changed - " +
                        "Admin: ${filter.adminFilterType.name}, " +
                        "Tag: ${filter.selectedTagFilter?.name}"
            )
            uiState.value = uiState.value.copy(currentFilter = filter)
            when (filter.questionListType) {
                QuestionListType.USERS_QUESTIONS, QuestionListType.USERS_ANSWERS -> {
                    if (filter.userId.isNullOrBlank()) {
                        kotlinx.coroutines.flow.flowOf(emptyList())
                    } else {
                        getQuestionsPagedByCreatedAtUseCase.invoke(
                            isInitial = isInitial,
                            moderationStatus = filter.adminFilterType.toModerationStatusOrNull(),
                            tag = filter.selectedTagFilter,
                            questionListType = filter.questionListType,
                            userId = filter.userId,
                        )
                    }
                }

                else -> {
                    getQuestionsPagedByCreatedAtUseCase.invoke(
                        isInitial,
                        moderationStatus = filter.adminFilterType.toModerationStatusOrNull(),
                        tag = filter.selectedTagFilter
                    )
                }
            }.map { questionsList: List<QuestionOperationResponseBody> ->
                questionsList.map { question ->
                    question.toUiState(base = QuestionViewUiState(isLoading = false))
                }
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is QuestionListEvent) {
            when (event) {
                QuestionListEvent.OnRefreshClicked -> listViewState.value.onRefresh?.invoke()
                is QuestionListEvent.OnItemClicked -> onItemClicked(event.item)
                is QuestionListEvent.OnTagFilterChanged -> setTagFilter(event.tag)
            }
        }
    }

    private fun onItemClicked(item: QuestionViewUiState) {
        // Branch based on future questionType when added to UI state
        when (item.questionType) {
            QuestionType.SINGLE_CHOICE -> {}
        }
    }

    fun setAdminFilter(filterType: QuestionListAdminFilterType) {
        adminFilterType.value = filterType
        val filter = queFilter.value
        if (filter == null) {
            queFilter.value =
                QueFilter(adminFilterType = filterType, selectedTagFilter = null)
            return
        }

        if (filter.adminFilterType == filterType) return
        queFilter.value = filter.copy(adminFilterType = filterType)
    }

    fun setTagFilter(tag: QueTag?) {
        val filter = queFilter.value
        if (filter == null) {
            queFilter.value = QueFilter(
                adminFilterType = QuestionListAdminFilterType.APPROVED_ADMIN,
                selectedTagFilter = tag
            )
            return
        }
        if (filter.selectedTagFilter == tag) return
        queFilter.value = filter.copy(selectedTagFilter = tag)
    }

    fun clearAllFilters() {
        queFilter.value = null
    }

    fun setUserFilter(questionListType: QuestionListType, userId: String) {
        val filter = queFilter.value
        if (filter == null) {
            queFilter.value = QueFilter(
                questionListType = questionListType,
                userId = userId
            )
            return
        }
        queFilter.value = filter.copy(questionListType = questionListType, userId = userId)
    }

    fun onQuestionEvent(event: QuestionViewEvent) {
        questionEventHandlerUseCase.handleQuestionEvent(event, listOperationDelegate.listUiState)
    }
}
