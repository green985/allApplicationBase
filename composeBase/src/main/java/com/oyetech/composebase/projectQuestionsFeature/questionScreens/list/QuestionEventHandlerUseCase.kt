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
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
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
        when (event) {
            is QuestionViewEvent.OnOptionSelected -> {
                handleOptionSelected(event, listUiState)
            }

            is QuestionViewEvent.OnDeleteAnswerClicked -> {
                handleDeleteAnswer(event)
            }

            is QuestionViewEvent.OnAcceptClicked -> {
                updateAdminOperationClicked(event.questionId, listUiState)
                scope.launch(appDispatchers.io) {
                    questionSupabaseRepository.updateQuestionStatus(
                        QuestionStatusUpdateRequest(
                            event.questionId,
                            ModerationStatus.APPROVED
                        )
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.OnDeclineClicked -> {
                updateAdminOperationClicked(event.questionId, listUiState)
                scope.launch(appDispatchers.io) {
                    questionSupabaseRepository.updateQuestionStatus(
                        QuestionStatusUpdateRequest(
                            event.questionId,
                            ModerationStatus.DECLINED
                        )
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.OnEditClicked -> {
                val route =
                    "${QuestionAppProjectRoutes.QuestionCreateQuestionPage.route}?questionId=${event.questionId}"
                navigationUseCase.navigateTo(route)
            }

            is QuestionViewEvent.OnPendingClicked -> {
                updateAdminOperationClicked(event.questionId, listUiState)
                scope.launch(appDispatchers.io) {
                    questionSupabaseRepository.updateQuestionStatus(
                        QuestionStatusUpdateRequest(
                            event.questionId,
                            ModerationStatus.PENDING
                        )
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.SetAdminMode -> {
                setAdminMode(event.isAdminView)
            }

            is QuestionViewEvent.SetAdminFilterType -> {
                setAdminFilterType(event.adminFilterType)
            }

            QuestionViewEvent.CancelClicked -> TODO()
            QuestionViewEvent.OnErrorDismiss -> TODO()
            is QuestionViewEvent.OnTagRemoved -> TODO()
            is QuestionViewEvent.OnTagSelected -> TODO()
            is QuestionViewEvent.OnTagSelectedForCreateQuestion -> TODO()
            QuestionViewEvent.SubmitClicked -> TODO()
            is QuestionViewEvent.TitleChanged -> {
                Timber.d("Title changed: ${event.value}")
            }

            is QuestionViewEvent.FormIdChanged -> TODO()
        }
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

            val questionUiState = listUiState.value.items.find {
                it.questionId == event.questionId
            }
            val formId = questionUiState?.formId

            val answer = QueAnswer(
                questionId = event.questionId,
                formId = formId,
                type = QuestionType.SINGLE_CHOICE,
                selectedOptionIds = listOf(event.optionId),
                numericValue = null,
                textValue = null,
                userId = uid,
            )
            answerUseCase.submitAnswer(answer).asResult()
                .collectLatest { /* updated in repo state */ }
        }
        Timber.d("Option selected: ${event.optionId} for question: ${event.questionId}")
    }

    private fun handleDeleteAnswer(event: QuestionViewEvent.OnDeleteAnswerClicked) {
        scope.launch(appDispatchers.io) {
            val uid = userRepository.getUserId()
            if (uid.isBlank()) return@launch
            answerUseCase.deleteAnswer(uid, event.questionId)
                .collectLatest { /* updated in repo state */ }
        }
    }

    private fun updateAdminOperationClicked(
        questionId: String,
        listUiState: MutableStateFlow<GenericListState<QuestionViewUiState>>,
    ) {
        listUiState.updateSingleItem(
            predicate = { it.questionId == questionId },
            transform = { it.copy(adminOperationClicked = true) }
        )
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
            val ans = q.questionId?.let { answersIdx[it] }
            val selected = ans?.selectedOptionIds?.firstOrNull()
            val newIsAnswered = selected != null

            if (q.isAnsweredByUser != newIsAnswered || q.selectedAnswer != selected) {
                q.copy(
                    isAnsweredByUser = newIsAnswered,
                    selectedAnswer = selected
                )
            } else q
        }
    }
}
