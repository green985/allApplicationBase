package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.updateSingleItem
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.domain.useCases.AnswerUseCase
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class QuestionEventHandlerUseCase(
    private val scope: CoroutineScope,
    private val adminViewState: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val adminFilterType: MutableStateFlow<QuestionListAdminFilterType> = MutableStateFlow(
        QuestionListAdminFilterType.APPROVED_ADMIN
    ),
) : KoinComponent {

    private val navigationUseCase: NavigationUseCase by inject()
    private val userRepository: AuthOperationRepository by inject()
    private val answerUseCase: AnswerUseCase by inject()
    private val appDispatchers: AppDispatchers by inject()
    private val questionSupabaseRepository: QuestionSupabaseRepository by inject()

    fun handleQuestionEvent(
        event: QuestionViewEvent,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        val moderationUpdate = event.toModerationStatusUpdateOrNull()

        when {
            event is QuestionViewEvent.OnOptionSelected -> handleOptionSelected(event, listUiState)
            event is QuestionViewEvent.OnDeleteAnswerClicked -> handleDeleteAnswer(
                event,
                listUiState
            )

            event is QuestionViewEvent.OnEditClicked -> navigateToEditQuestion(event.questionId)
            event is QuestionViewEvent.SetAdminMode -> setAdminMode(event.isAdminView)
            event is QuestionViewEvent.SetAdminFilterType -> setAdminFilterType(event.adminFilterType)
            event is QuestionViewEvent.OnErrorDismiss -> clearAllItemErrors(listUiState)
            moderationUpdate != null -> {
                handleAdminStatusUpdate(
                    questionId = moderationUpdate.questionId,
                    moderationStatus = moderationUpdate.status,
                    listUiState = listUiState
                )
            }

            else -> handlePassiveQuestionEvent(event)
        }
    }

    private data class ModerationStatusUpdate(
        val questionId: String,
        val status: ModerationStatus,
    )

    private fun QuestionViewEvent.toModerationStatusUpdateOrNull(): ModerationStatusUpdate? {
        return when (this) {
            is QuestionViewEvent.OnAcceptClicked -> {
                ModerationStatusUpdate(
                    questionId = this.questionId,
                    status = ModerationStatus.APPROVED
                )
            }

            is QuestionViewEvent.OnDeclineClicked -> {
                ModerationStatusUpdate(
                    questionId = this.questionId,
                    status = ModerationStatus.DECLINED
                )
            }

            is QuestionViewEvent.OnPendingClicked -> {
                ModerationStatusUpdate(
                    questionId = this.questionId,
                    status = ModerationStatus.PENDING
                )
            }

            else -> null
        }
    }

    private fun navigateToEditQuestion(questionId: String) {
        val route =
            "${QuestionAppProjectRoutes.QuestionCreateQuestionPage.route}?questionId=$questionId"
        navigationUseCase.navigateTo(route)
    }

    private fun handlePassiveQuestionEvent(event: QuestionViewEvent) {
        when (event) {
            is QuestionViewEvent.OnTagSelected -> {
                val route = if (adminViewState.value) {
                    QuestionListNavigationHelper.buildQuestionListWithFiltersRoute(
                        tag = event.tag,
                        adminFilterType = adminFilterType.value
                    )
                } else {
                    QuestionListNavigationHelper.buildQuestionListWithTagRoute(event.tag)
                }
                navigationUseCase.navigateTo(route)
            }

            is QuestionViewEvent.OnTagRemoved -> {
                Timber.d(
                    "OnTagRemoved is create-question specific; " +
                            "ignored in list context: ${event.tag.id}"
                )
            }

            is QuestionViewEvent.OnTagSelectedForCreateQuestion -> {
                Timber.d(
                    "OnTagSelectedForCreateQuestion is create-question specific; " +
                            "ignored in list context: ${event.tag.id}"
                )
            }

            QuestionViewEvent.CancelClicked -> {
                Timber.d("CancelClicked is not handled in question list context")
            }

            QuestionViewEvent.SubmitClicked -> {
                Timber.d("SubmitClicked is not handled in question list context")
            }

            is QuestionViewEvent.TitleChanged -> {
                Timber.d("Title changed: ${event.value}")
            }

            is QuestionViewEvent.FormIdChanged -> {
                Timber.d("FormId changed in list context: ${event.value}")
            }

            else -> Unit
        }
    }

    private fun handleAdminStatusUpdate(
        questionId: String,
        moderationStatus: ModerationStatus,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        updateItem(questionId, listUiState) {
            it.copy(
                adminOperationClicked = true,
                isLoading = true,
                isError = false,
                errorText = ""
            )
        }

        scope.launch(appDispatchers.io) {
            questionSupabaseRepository
                .updateQuestionStatus(
                    QuestionStatusUpdateRequest(
                        questionId,
                        moderationStatus
                    )
                )
                .asResult()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            updateItem(questionId, listUiState) {
                                it.copy(
                                    adminOperationClicked = true,
                                    isLoading = false,
                                    isError = false,
                                    errorText = "",
                                    moderationStatus = moderationStatus
                                )
                            }
                        },
                        onFailure = { error ->
                            updateItem(questionId, listUiState) {
                                it.copy(
                                    adminOperationClicked = false,
                                    isLoading = false,
                                    isError = true,
                                    errorText = ErrorMessage.fetchErrorMessage(error.message)
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun updateItem(
        questionId: String,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
        transform: (QuestionViewUiState) -> QuestionViewUiState,
    ) {
        listUiState.updateSingleItem(
            predicate = { it.questionId == questionId },
            transform = transform
        )
    }

    private fun handleOptionSelected(
        event: QuestionViewEvent.OnOptionSelected,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        scope.launch(appDispatchers.io) {
            val uid = userRepository.getUserId()
            if (uid.isBlank()) {
                Timber.d("User ID is blank, cannot submit answer")
                return@launch
            }

            val alreadyAnswered = answerUseCase.answersState.value.any {
                it.userId == uid && it.questionId == event.questionId
            }
            if (alreadyAnswered) return@launch

            updateQuestionOperationState(
                questionId = event.questionId,
                listUiState = listUiState,
                isLoading = true,
                isError = false,
                errorText = ""
            )

            val questionUiState = listUiState.value.items.find {
                it.questionId == event.questionId
            }

            val answer = QueAnswer(
                questionId = event.questionId,
                formId = questionUiState?.formId,
                type = QuestionType.SINGLE_CHOICE,
                selectedOptionIds = listOf(event.optionId),
                numericValue = null,
                textValue = null,
                userId = uid,
            )

            answerUseCase.submitAnswer(answer).asResult().collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateQuestionOperationState(
                            questionId = event.questionId,
                            listUiState = listUiState,
                            isLoading = false,
                            isError = false,
                            errorText = ""
                        )
                    },
                    onFailure = { error ->
                        updateQuestionOperationState(
                            questionId = event.questionId,
                            listUiState = listUiState,
                            isLoading = false,
                            isError = true,
                            errorText = ErrorMessage.fetchErrorMessage(error.message)
                        )
                    }
                )
            }
        }
        Timber.d("Option selected: ${event.optionId} for question: ${event.questionId}")
    }

    private fun handleDeleteAnswer(
        event: QuestionViewEvent.OnDeleteAnswerClicked,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        scope.launch(appDispatchers.io) {
            val uid = userRepository.getUserId()
            if (uid.isBlank()) return@launch

            updateQuestionOperationState(
                questionId = event.questionId,
                listUiState = listUiState,
                isLoading = true,
                isError = false,
                errorText = ""
            )

            answerUseCase.deleteAnswer(uid, event.questionId).asResult().collectLatest { result ->
                result.fold(
                    onSuccess = {
                        updateQuestionOperationState(
                            questionId = event.questionId,
                            listUiState = listUiState,
                            isLoading = false,
                            isError = false,
                            errorText = ""
                        )
                    },
                    onFailure = { error ->
                        updateQuestionOperationState(
                            questionId = event.questionId,
                            listUiState = listUiState,
                            isLoading = false,
                            isError = true,
                            errorText = ErrorMessage.fetchErrorMessage(error.message)
                        )
                    }
                )
            }
        }
    }

    private fun updateQuestionOperationState(
        questionId: String,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
        isLoading: Boolean,
        isError: Boolean,
        errorText: String,
    ) {
        updateItem(questionId, listUiState) {
            it.copy(
                isLoading = isLoading,
                isError = isError,
                errorText = errorText
            )
        }
    }

    private fun clearAllItemErrors(
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        val updatedItems = listUiState.value.items.map { item ->
            if (item.isError || item.errorText.isNotBlank()) {
                item.copy(isError = false, errorText = "")
            } else {
                item
            }
        }

        listUiState.value = listUiState.value.copy(items = updatedItems.toImmutableList())
    }

    private fun setAdminMode(isAdminView: Boolean) {
        adminViewState.value = isAdminView
        Timber.d("Admin view mode set to: $isAdminView")
    }

    private fun setAdminFilterType(filterType: QuestionListAdminFilterType) {
        adminFilterType.value = filterType
        Timber.d("Admin filter type set to: $filterType")
    }
}

fun Flow<List<QuestionViewUiState>>.questionAnswerOverlayFlow(
    answersState: StateFlow<List<QueAnswer>>,
): Flow<List<QuestionViewUiState>> {
    val answersIndexedFlow =
        answersState
            .map { list -> list.associateBy { it.questionId } }
            .distinctUntilChanged()

    return combine(this, answersIndexedFlow) { questions, answersIdx ->
        if (questions.isEmpty()) return@combine questions

        questions.map { q ->
            val ans = answersIdx[q.questionId]
            val selected = ans?.selectedOptionIds?.firstOrNull()
            val newIsAnswered = selected != null

            if (q.isAnsweredByUser != newIsAnswered || q.selectedAnswer != selected) {
                q.copy(
                    isAnsweredByUser = newIsAnswered,
                    selectedAnswer = selected
                )
            } else {
                q
            }
        }
    }
}
