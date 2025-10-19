package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionPagerVm(
    appDispatchers: AppDispatchers,
    val questionListVm: QuestionListVm,
) : BaseViewModel(appDispatchers) {

    private val _uiState = MutableStateFlow(QuestionPagerUiState())
    val uiState: StateFlow<QuestionPagerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(getDispatcherIo()) {
        }
    }

    override fun onEvent(event: Any) {
        if (event is QuestionPagerEvent) {
            when (event) {
                is QuestionPagerEvent.OnTagFilterChanged -> {
                }
            }
        }
    }

}

data class QuestionPagerUiState(
    val currentFilter: QueFilter = QueFilter.DEFAULT,
    val currentPage: Int = 0,
)

sealed class QuestionPagerEvent {
    data class OnTagFilterChanged(val tag: QueTag?) : QuestionPagerEvent()
}
