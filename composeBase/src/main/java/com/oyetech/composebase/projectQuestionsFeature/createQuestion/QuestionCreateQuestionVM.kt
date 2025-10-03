package com.oyetech.composebase.projectQuestionsFeature.createQuestion

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.mappers.toOperationBody
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.CancelClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.NoClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.SubmitClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.TitleChanged
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.YesClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.firebaseDB.firebaseDB.question.FirebaseQuestionOperationRepositoryImpl
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuestionCreateQuestionVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
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
                CancelClicked -> TODO()
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
        uiState.updateState { copy(isLoading = true, isError = false, errorText = "") }
        viewModelScope.launch(getDispatcherIo()) {
            val repo = FirebaseQuestionOperationRepositoryImpl()
            repo.createQuestion(currentQuestion.toOperationBody())
                .asResult()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            uiState.updateState { copy(isLoading = false, isSubmitted = true) }
                            navigationUseCase.navigate("back")
                            uiEvent.tryEmit(QuestionCreateQuestionUiEvent.OnSubmitSuccess)
                        },
                        onFailure = {
                            uiState.updateState {
                                copy(
                                    isLoading = false,
                                    isError = true,
                                    errorText = LanguageKey.generalErrorText
                                )
                            }
                            uiEvent.tryEmit(
                                QuestionCreateQuestionUiEvent.OnSubmitError(
                                    it.message ?: LanguageKey.generalErrorText
                                )
                            )
                        }
                    )
                }
        }
    }
}
