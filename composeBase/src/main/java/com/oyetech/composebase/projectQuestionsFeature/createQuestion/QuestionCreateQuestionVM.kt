package com.oyetech.composebase.projectQuestionsFeature.createQuestion

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun onQuestionEvent(event: QuestionViewEvent) {
        when (event) {
            QuestionViewEvent.CancelClicked -> TODO()
            QuestionViewEvent.NoClicked -> TODO()
            QuestionViewEvent.SubmitClicked -> TODO()
            is QuestionViewEvent.TitleChanged -> {
                questionUiState.updateState {
                    copy(titleText = event.value)
                }
            }

            QuestionViewEvent.YesClicked -> TODO()
        }
    }

    override fun onEvent(event: Any) {
        if (event is QuestionCreateQuestionEvent) {
            when (event) {

                is QuestionCreateQuestionEvent.OnTitleChange -> {
                    onQuestionEvent(QuestionViewEvent.TitleChanged(event.text))
                }

                QuestionCreateQuestionEvent.OnSubmit -> submit()
                QuestionCreateQuestionEvent.OnRetry -> Unit
                QuestionCreateQuestionEvent.OnScreenOut -> Unit
            }
        }
    }

    private fun submit() {
        val current = uiState.value
        if (current.titleText.isBlank() || current.descriptionText.isBlank()) return
        uiState.updateState { copy(isLoading = true) }
        viewModelScope.launch(getDispatcherIo()) {
            // TODO: integrate with repository when available
            // Simulate success
            uiState.updateState { copy(isLoading = false, isSubmitted = true) }
            navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionAppHomepage.route)
            uiEvent.tryEmit(QuestionCreateQuestionUiEvent.OnSubmitSuccess)
        }
    }
}
