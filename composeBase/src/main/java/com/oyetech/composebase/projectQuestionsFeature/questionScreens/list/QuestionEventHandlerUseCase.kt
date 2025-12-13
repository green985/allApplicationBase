package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.helpers.listOperations.ListOperationDelegate
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import com.oyetech.models.questionProject.questionOperation.QuestionType
import com.oyetech.tools.coroutineHelper.AppDispatchers
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
    private val repository: FirebaseQuestionOperationRepository by inject()
    private val userRepository: FirebaseUserRepository by inject()
    private val answerRepository: FirebaseQuestionAnswerRepository by inject()
    private val appDispatchers: AppDispatchers by inject()

    fun handleQuestionEvent(
        event: QuestionViewEvent,
        listOperationDelegate: ListOperationDelegate<QuestionViewUiState>,
    ) {
        when (event) {
            is QuestionViewEvent.OnOptionSelected -> {
                handleOptionSelected(event)
            }

            is QuestionViewEvent.OnDeleteAnswerClicked -> {
                handleDeleteAnswer(event)
            }

            is QuestionViewEvent.OnAcceptClicked -> {
                updateAdminOperationClicked(event.questionId, listOperationDelegate)
                scope.launch(appDispatchers.io) {
                    repository.updateQuestionStatus(
                        event.questionId,
                        ModerationStatus.APPROVED
                    ).collectLatest { /* no-op */ }
                }
            }

            is QuestionViewEvent.OnDeclineClicked -> {
                updateAdminOperationClicked(event.questionId, listOperationDelegate)
                scope.launch(appDispatchers.io) {
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
                updateAdminOperationClicked(event.questionId, listOperationDelegate)
                scope.launch(appDispatchers.io) {
                    repository.updateQuestionStatus(
                        event.questionId,
                        ModerationStatus.PENDING
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
            is QuestionViewEvent.TitleChanged -> TODO()
        }
    }

    private fun handleOptionSelected(event: QuestionViewEvent.OnOptionSelected) {
        scope.launch(appDispatchers.io) {
            val uid = userRepository.getUserId()
            if (uid.isBlank()) {
                Timber.d("User ID is blank, cannot submit answer")
                return@launch
            }
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

    private fun handleDeleteAnswer(event: QuestionViewEvent.OnDeleteAnswerClicked) {
        scope.launch(appDispatchers.io) {
            val uid = userRepository.getUserId()
            if (uid.isBlank()) return@launch
            answerRepository.deleteAnswer(uid, event.questionId)
                .collectLatest { /* updated in repo state */ }
        }
    }

    private fun updateAdminOperationClicked(
        questionId: String,
        listOperationDelegate: ListOperationDelegate<QuestionViewUiState>,
    ) {
        listOperationDelegate.listUiState.value.items.find { it.questionId == questionId }
            ?.let { item ->
                val updated = item.copy(adminOperationClicked = true)
                val currentList = listOperationDelegate.listUiState.value.items.toMutableList()
                val index = currentList.indexOf(item)
                if (index != -1) {
                    currentList[index] = updated
                    listOperationDelegate.updateList(currentList)
                }
            }
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