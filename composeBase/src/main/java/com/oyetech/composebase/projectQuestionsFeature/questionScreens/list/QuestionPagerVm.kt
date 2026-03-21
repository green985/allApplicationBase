package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionTagCatalog
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionPagerVm(
    appDispatchers: AppDispatchers,
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
    val currentFilterType: QueTag? = null,
    val tabs: ImmutableList<Pair<QueTag, String>> = QuestionTagCatalog.questionMeaningList.map {
        Pair(it, it.name)
    }.toImmutableList(),
)

sealed class QuestionPagerEvent {
    data class OnTagFilterChanged(val tag: QueTag?) : QuestionPagerEvent()
}
