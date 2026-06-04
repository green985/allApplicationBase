package com.oyetech.composebase.sharedScreens.stopwatch

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class StopwatchDurationVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
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
        navigationUseCase.navigateTo(AppRoute.StopwatchScreen(minutes = minutes))
    }
}

