package com.oyetech.composebase.sharedScreens.stopwatch

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class StopwatchDurationVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val stopwatchOperationUseCase: StopwatchOperationUseCase,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(StopwatchDurationUiState())

    override fun onEvent(event: Any) {
        if (event is StopwatchDurationEvent) {
            when (event) {
                is StopwatchDurationEvent.OnDurationSelected -> onDurationSelected(event.minutes)
            }
        }
    }

    private fun onDurationSelected(minutes: Int) {
        uiState.updateState { copy(selectedMinutes = minutes) }

        viewModelScope.launch(getDispatcherIo()) {
            stopwatchOperationUseCase.startCountdown(minutes).collect {
                uiState.updateState {
                    val mins = it.remainingSeconds / 60
                    val secs = it.remainingSeconds % 60
                    copy(
                        remainingMinutes = mins,
                        remainingSeconds = secs,
                        formattedTime = "${mins.toString().padStart(2, '0')}:${
                            secs.toString().padStart(2, '0')
                        }",
                    )
                }
            }
        }
//        navigationUseCase.navigateTo(AppRoute.StopwatchScreen(minutes = minutes))
    }
}

