package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class AdminApproveQuestionVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(AdminApproveQuestionUiState())
    val uiEvent = MutableSharedFlow<AdminApproveQuestionUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        viewModelScope.launch(getDispatcherIo()) {
        }
    }

    override fun onEvent(event: Any) {
        if (event is AdminApproveQuestionEvent) {
            when (event) {
                is AdminApproveQuestionEvent.OnFilterSelected -> {
                    uiState.updateState { copy(currentFilterType = event.filterType) }
                }

                is AdminApproveQuestionEvent.OnTagFilterChanged -> {

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
