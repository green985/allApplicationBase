package com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.CancelClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.NoClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.SubmitClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.TitleChanged
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.YesClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.toOperationBody
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class QuestionCreateQuestionVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val questionRepository: FirebaseQuestionOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuestionCreateQuestionScreenUiState())
    val uiEvent = MutableSharedFlow<QuestionCreateQuestionUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val questionUiState = MutableStateFlow(QuestionViewUiState())

    override fun onEvent(event: Any) {
        if (event is QuestionViewEvent) {
            when (event) {
                CancelClicked -> navigationUseCase.navigate("back")
                QuestionViewEvent.OnErrorDismiss -> {
                    uiState.updateState { copy(errorText = "") }
                }

                NoClicked -> TODO()
                SubmitClicked -> submit()
                is TitleChanged -> {
                    questionUiState.updateState {
                        copy(titleText = event.value)
                    }
                }

                YesClicked -> TODO()
            }
        }
    }

    private fun submit() {
        val currentQuestion = questionUiState.value
        if (currentQuestion.titleText.isBlank()) return
        uiState.updateState { copy(isLoading = true, errorText = "") }
        viewModelScope.launch(getDispatcherIo()) {
            questionRepository.createQuestion(currentQuestion.toOperationBody())
                .asResult()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            Timber.d("Question created")
                            uiState.updateState { copy(isLoading = false, isSubmitted = true) }
                            navigationUseCase.navigate("back")
                            snackbarDelegate.triggerSnackbarState(
                                message = LanguageKey.questionAddedSuccessfullyText
                            )
                        },
                        onFailure = {
                            Timber.d(it)
                            uiState.updateState {
                                copy(
                                    isLoading = false,
                                    errorText = LanguageKey.generalErrorText
                                )
                            }
                            snackbarDelegate.triggerSnackbarState(
                                message = it.message ?: LanguageKey.generalErrorText
                            )
                        }
                    )
                }
        }
    }
}
