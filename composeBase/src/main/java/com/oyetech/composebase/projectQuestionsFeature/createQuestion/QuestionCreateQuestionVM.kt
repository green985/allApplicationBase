package com.oyetech.composebase.projectQuestionsFeature.createQuestion

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.CancelClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.NoClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.SubmitClicked
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.TitleChanged
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent.YesClicked
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
        val current = uiState.value
        if (current.titleText.isBlank()) return
        uiState.updateState { copy(isLoading = true) }
        viewModelScope.launch(getDispatcherIo()) {
            // TODO: integrate with repository when available
            // Simulate success
            uiState.updateState { copy(isLoading = false, isSubmitted = true) }
            navigationUseCase.navigate("back")
            uiEvent.tryEmit(QuestionCreateQuestionUiEvent.OnSubmitSuccess)
        }
    }
}
