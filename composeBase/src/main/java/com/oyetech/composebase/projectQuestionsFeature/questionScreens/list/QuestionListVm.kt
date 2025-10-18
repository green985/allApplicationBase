package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("TooManyFunctions")
class QuestionListVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val repository: FirebaseQuestionOperationRepository,
    private val userRepository: FirebaseUserRepository,
    private val answerRepository: FirebaseQuestionAnswerRepository,
    private val questionUseCase: com.oyetech.domain.useCases.QuestionUseCase,
) : BaseListViewModel<QuestionViewUiState>(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())
    private val queFilter = MutableStateFlow(QueFilter.DEFAULT)

    override val listViewState: MutableStateFlow<GenericListState<QuestionViewUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = combine(
                    questionUseCase.getQuestionListWithUpdates(),
                    answerRepository.answersState,
                    queFilter,
                ) { questions, answers, filter ->
                    val adminFiltered = filterQuestionsByStatus(questions, filter.adminFilterType)
                    val tagFiltered = filterQuestionsByTag(adminFiltered, filter.selectedTagFilter)

                    overlayAnswers(tagFiltered, answers, filter.adminFilterType)
                },
                refreshDataFlow = combine(
                    questionUseCase.getQuestionListWithUpdates(),
                    answerRepository.answersState,
                    queFilter,
                ) { questions, answers, filter ->
                    val adminFiltered = filterQuestionsByStatus(questions, filter.adminFilterType)
                    val tagFiltered = filterQuestionsByTag(adminFiltered, filter.selectedTagFilter)
                    overlayAnswers(tagFiltered, answers, filter.adminFilterType)
                }
            )
        )

    init {
        // Fetch user answers on start
        viewModelScope.launch(getDispatcherIo()) {
            queFilter.collectLatest { filter ->
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
    }

    override fun onEvent(event: Any) {
        if (event is QuestionListEvent) {
            when (event) {
                QuestionListEvent.OnRefreshClicked -> refreshList()
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
        filter: QuestionListAdminFilterType,
    ): List<QuestionViewUiState> {
        val answerMap = answers.associateBy { it.questionId }
        return questions.map { q ->
            var ui = q.toUiState(base = QuestionViewUiState(isLoading = false))
            val ans = answerMap[q.questionId]
            if (ans != null) {
                val selected = ans.selectedOptionIds?.firstOrNull()
                ui = ui.copy(isAnsweredByUser = selected != null, selectedAnswer = selected)
            }

            ui = ui.copy(adminFilterType = filter)
            ui
        }
    }

    fun setAdminFilter(filterType: QuestionListAdminFilterType) {
        queFilter.value = queFilter.value.copy(adminFilterType = filterType)
    }

    fun setTagFilter(tag: QueTag?) {
        queFilter.value = queFilter.value.copy(selectedTagFilter = tag)
    }

    fun clearAllFilters() {
        queFilter.value = QueFilter.DEFAULT
    }

    fun approveAllPending() {
        viewModelScope.launch(getDispatcherIo()) {
            val items = listViewState.value.items
            items.forEach { item ->
                repository.updateQuestionStatus(
                    item.questionId,
                    ModerationStatus.APPROVED
                ).collectLatest { /* no-op */ }
            }
        }
    }

    fun declineAllPending() {
        viewModelScope.launch(getDispatcherIo()) {
            val items = listViewState.value.items
            items.forEach { item ->
                repository.updateQuestionStatus(
                    item.questionId,
                    ModerationStatus.DECLINED
                ).collectLatest { /* no-op */ }
            }
        }
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
                Timber.d("Option selected: ${'$'}{event.optionId} for question ${'$'}{event.questionId}")
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
                navigationUseCase.navigate(route)
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

            else -> {
                Timber.d("Unhandled QuestionViewEvent in ListVM: ${event.javaClass.simpleName}")
            }
        }
    }

    private fun updateAdminOperationClicked(questionId: String) {
        listViewState.value.items.find { it.questionId == questionId }?.let { item ->
            val updated = item.copy(adminOperationClicked = true)
            val currentList = listViewState.value.items.toMutableList()
            val index = currentList.indexOf(item)
            if (index != -1) {
                currentList[index] = updated
                listViewState.value =
                    listViewState.value.copy(items = currentList.toImmutableList())
            }
        }
    }

    private fun filterQuestionsByStatus(
        questions: List<QuestionOperationResponseBody>,
        filter: QuestionListAdminFilterType,
    ): List<QuestionOperationResponseBody> {
        return when (filter) {
            QuestionListAdminFilterType.ALL -> questions
            QuestionListAdminFilterType.APPROVED_ADMIN ->
                questions.filter { it.moderationStatus == ModerationStatus.APPROVED }

            QuestionListAdminFilterType.DECLINED_ADMIN ->
                questions.filter { it.moderationStatus == ModerationStatus.DECLINED }

            QuestionListAdminFilterType.PENDING_ADMIN ->
                questions.filter { it.moderationStatus == ModerationStatus.PENDING }
        }
    }

    private fun filterQuestionsByTag(
        questions: List<QuestionOperationResponseBody>,
        tagFilter: QueTag?,
    ): List<QuestionOperationResponseBody> {
        if (tagFilter == null) return questions
        return questions.filter { question ->
            question.tags?.any { it.id == tagFilter.id } == true
        }
    }
}
