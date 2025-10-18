package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionFilterOperationHelper
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListVm
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AdminApproveQuestionVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    val questionListVm: QuestionListVm,
    val filterHelper: QuestionFilterOperationHelper,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(AdminApproveQuestionUiState())
    val uiEvent = MutableSharedFlow<AdminApproveQuestionUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        viewModelScope.launch(getDispatcherIo()) {
            filterHelper.queFilter.collectLatest { filter ->
                questionListVm.setAdminFilter(filter.adminFilterType)
                questionListVm.setTagFilter(filter.selectedTagFilter)
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is AdminApproveQuestionEvent) {
            when (event) {
                is AdminApproveQuestionEvent.OnFilterSelected -> {
                    questionListVm.setAdminFilter(event.filterType)
                    uiState.updateState { copy(currentFilterType = event.filterType) }
                }

                is AdminApproveQuestionEvent.OnTagFilterChanged -> {
                    filterHelper.setTagFilter(event.tag)
                }

                AdminApproveQuestionEvent.OnApproveAll -> {
                    questionListVm.approveAllPending()
                }

                AdminApproveQuestionEvent.OnDeclineAll -> {
                    questionListVm.declineAllPending()
                }

                AdminApproveQuestionEvent.OnRefreshClicked -> refresh()
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch(getDispatcherIo()) {
            // TODO: integrate repository to fetch pending count
            uiState.updateState { copy(pendingCountText = pendingCountText) }
        }
    }
}
