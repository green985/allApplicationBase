package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.helpers.listOperations.GetQuestionsPagedByCreatedAtUseCase
import com.oyetech.composebase.helpers.listOperations.ListOperationDelegate
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toUiState
import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionType
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
class QuestionListVm2(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val repository: FirebaseQuestionOperationRepository,
    private val userRepository: FirebaseUserRepository,
    private val answerRepository: FirebaseQuestionAnswerRepository,
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

    init {
        // Fetch user answers on start
        viewModelScope.launch(getDispatcherIo()) {
            queFilter.filterNotNull().collectLatest { filter ->
                Timber.d(
                    "Filter changed - " +
                            "Admin: ${filter.adminFilterType.name}, Tag: ${filter.selectedTagFilter?.name}"
                )
                uiState.value = uiState.value.copy(currentFilter = filter)
            }
        }
        // todo will be fixed later...
        // Fetch user answers on start
        viewModelScope.launch(getDispatcherIo()) {
            val uid = userRepository.getUserId()
            if (uid.isNotBlank()) {
                answerRepository.getAnswersByUser(uid)
                    .collectLatest { /* repo updates its own state */ }
            }
        }

        viewModelScope.launch(getDispatcherIo()) {
            questionItemsFlow(listOperationDelegate).filter { it.isNotEmpty() }
                .getQuestionTransformerFlow()
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
    ): kotlinx.coroutines.flow.Flow<List<QuestionViewUiState>> {
        return listOperationDelegate.listUiState
            .map { it.items }
            .distinctUntilChanged()
    }

    private fun Flow<List<QuestionViewUiState>>.getQuestionTransformerFlow(): Flow<List<QuestionViewUiState>> {
        return this.combine(answerRepository.answersState) { questions, answers ->
            Timber.d("Combining answers with admin view: ${answers.size}")
            if (questions.isEmpty()) {
                return@combine questions
            }
            questions.map { question ->
                var ui = question
                val ans =
                    answers.find { answ -> answ.questionId == question.questionId }
                if (ans != null) {
                    val selected = ans.selectedOptionIds?.firstOrNull()
                    ui = ui.copy(
                        isAnsweredByUser = selected != null,
                        selectedAnswer = selected
                    )
                }
                ui
            }
        }.combine(adminViewState) { questions, isAdminView ->
            questions.map { question ->
                question.copy(isAdminView = isAdminView)
            }
        }.combine(adminFilterType) { questions, filterType ->
            Timber.d("Combining adminFilterType with admin view: ${questions.size}")
            questions.map { question ->
                question.copy(adminFilterType = filterType)
            }
        }
    }

    fun getQuestionDataFlow(isInitial: Boolean): Flow<List<QuestionViewUiState>> {
        return queFilter.filterNotNull().flatMapLatest { filter ->
            when (filter.questionListType) {
                "USERS_QUESTIONS" -> {
                    if (filter.userId.isNullOrBlank()) {
                        kotlinx.coroutines.flow.flowOf(emptyList())
                    } else {
                        repository.getUserQuestions(filter.userId)
                    }
                }

                "USERS_ANSWERS" -> {
                    if (filter.userId.isNullOrBlank()) {
                        kotlinx.coroutines.flow.flowOf(emptyList())
                    } else {
                        repository.getUserAnsweredQuestions(filter.userId)
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

    private fun overlayAnswers(
        questions: List<QuestionOperationResponseBody>,
        answers: List<QueAnswer>,
    ): List<QuestionViewUiState> {
        val answerMap = answers.associateBy { it.questionId }
        return questions.map { q ->
            var ui = q.toUiState(base = QuestionViewUiState(isLoading = false))
            val ans = answerMap[q.questionId]
            if (ans != null) {
                val selected = ans.selectedOptionIds?.firstOrNull()
                ui = ui.copy(isAnsweredByUser = selected != null, selectedAnswer = selected)
            }

            ui
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

    fun setUserFilter(questionListType: String, userId: String) {
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
        when (event) {
            is QuestionViewEvent.OnOptionSelected -> {
                viewModelScope.launch(getDispatcherIo()) {
                    val uid = userRepository.getUserId()
                    if (uid.isBlank()) return@launch
                    val alreadyAnswered = answerRepository.answersState.value.any {
                        it.userId == uid && it.questionId == event.questionId
                    }
                    if (alreadyAnswered) return@launch
                    val answer = QueAnswer(
                        questionId = event.questionId,
                        type = QuestionType.SINGLE_CHOICE,
                        selectedOptionIds = listOf(event.optionId),
                        numericValue = null,
                        textValue = null,
                        userId = uid,
                        submittedAt = null,
                    )
                    answerRepository.submitAnswer(answer)
                        .collectLatest { /* updated in repo state */ }
                }
                Timber.d("Option selected: ${event.optionId} for question: ${event.questionId}")
            }

            is QuestionViewEvent.OnDeleteAnswerClicked -> {
                viewModelScope.launch(getDispatcherIo()) {
                    val uid = userRepository.getUserId()
                    if (uid.isBlank()) return@launch
                    answerRepository.deleteAnswer(uid, event.questionId)
                        .collectLatest { /* updated in repo state */ }
                }
            }

            is QuestionViewEvent.OnAcceptClicked -> {
                updateAdminOperationClicked(event.questionId)
                viewModelScope.launch(getDispatcherIo()) {
                    repository.updateQuestionStatus(
                        event.questionId,
                        ModerationStatus.APPROVED
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.OnDeclineClicked -> {
                updateAdminOperationClicked(event.questionId)
                viewModelScope.launch(getDispatcherIo()) {
                    repository.updateQuestionStatus(
                        event.questionId,
                        ModerationStatus.DECLINED
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.OnEditClicked -> {
                val route =
                    "${QuestionAppProjectRoutes.QuestionCreateQuestionPage.route}?questionId=${event.questionId}"
                navigationUseCase.navigateTo(route)
            }

            is QuestionViewEvent.OnPendingClicked -> {
                updateAdminOperationClicked(event.questionId)
                viewModelScope.launch(getDispatcherIo()) {
                    repository.updateQuestionStatus(
                        event.questionId,
                        ModerationStatus.PENDING
                    ).collectLatest { /* no-op */ }
                }
            }

            QuestionViewEvent.CancelClicked -> TODO()
            QuestionViewEvent.OnErrorDismiss -> TODO()
            is QuestionViewEvent.OnTagRemoved -> TODO()
            is QuestionViewEvent.OnTagSelected -> TODO()
            is QuestionViewEvent.OnTagSelectedForCreateQuestion -> TODO()
            is QuestionViewEvent.SetAdminMode -> {
                adminViewState.value = event.isAdminView
                Timber.d("Admin view mode set to: ${event.isAdminView}")
            }

            is QuestionViewEvent.SetAdminFilterType -> {
                adminFilterType.value = event.adminFilterType
                Timber.d("Admin view mode set to: ${event.adminFilterType}")
            }

            QuestionViewEvent.SubmitClicked -> TODO()
            is QuestionViewEvent.TitleChanged -> TODO()
        }
    }

    private fun updateAdminOperationClicked(questionId: String) {
        listViewState.value.items.find { it.questionId == questionId }?.let { item ->
            val updated = item.copy(adminOperationClicked = true)
            val currentList = listViewState.value.items.toMutableList()
            val index = currentList.indexOf(item)
            if (index != -1) {
                currentList[index] = updated
                listOperationDelegate.updateList(currentList)
            }
        }
    }
}
