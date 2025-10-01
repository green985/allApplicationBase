package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// UI state for Admin Approve Questions
data class AdminApproveQuestionUiState(
    val errorText: String = "",
    val isLoading: Boolean = false,
    val pendingCountText: String = "0 pending",
)

// UI events
sealed class AdminApproveQuestionUiEvent : BaseUIEvent() {
    data object OnIdle : AdminApproveQuestionUiEvent()
}

// View events
sealed class AdminApproveQuestionEvent : BaseEvent() {
    data object OnApproveAllClicked : AdminApproveQuestionEvent()
    data object OnRefreshClicked : AdminApproveQuestionEvent()
}

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

    override fun onEvent(event: Any) {
        if (event is AdminApproveQuestionEvent) {
            when (event) {
                AdminApproveQuestionEvent.OnApproveAllClicked -> approveAll()
                AdminApproveQuestionEvent.OnRefreshClicked -> refresh()
            }
        }
    }

    private fun approveAll() {
        viewModelScope.launch(getDispatcherIo()) {
            // TODO: integrate repository to approve
            uiState.updateState { copy(isLoading = true) }
            // Simulate complete
            uiState.updateState { copy(isLoading = false, pendingCountText = "0 pending") }
        }
    }

    private fun refresh() {
        viewModelScope.launch(getDispatcherIo()) {
            // TODO: integrate repository to fetch pending count
            uiState.updateState { copy(pendingCountText = pendingCountText) }
        }
    }
}
