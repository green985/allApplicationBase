package com.oyetech.composebase.sharedScreens.stopwatch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class StopwatchDurationVm(
    appDispatchers: AppDispatchers,
    private val stopwatchOperationUseCase: StopwatchOperationUseCase,
    private val appContext: Context,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(StopwatchDurationUiState())

    init {
        Timber.d("StopwatchDurationVm: init")
        observeTickState()
        observeFinished()
    }

    override fun onEvent(event: Any) {
        Timber.d("StopwatchDurationVm: onEvent -> $event")
        if (event is StopwatchDurationEvent) {
            when (event) {
                is StopwatchDurationEvent.OnDurationSelected -> onDurationSelected(event.minutes)
            }
        }
    }

    private fun onDurationSelected(minutes: Int) {
        Timber.d("StopwatchDurationVm: onDurationSelected minutes=$minutes")
        uiState.updateState { copy(selectedMinutes = minutes) }
        startTimerService(minutes)
    }

    @SuppressLint("NewApi")
    private fun startTimerService(minutes: Int) {
        Timber.d("StopwatchDurationVm: startTimerService minutes=$minutes packageName=${appContext.packageName}")
        val intent = Intent("com.oyetech.wear.ACTION_START_TIMER").apply {
            setPackage(appContext.packageName)
            putExtra("minutes", minutes)
        }
        ContextCompat.startForegroundService(appContext, intent)
    }

    private fun observeTickState() {
        Timber.d("StopwatchDurationVm: observeTickState started")
        viewModelScope.launch(getDispatcherIo()) {
            stopwatchOperationUseCase.tickState.collect { tick ->
                Timber.d(
                    "StopwatchDurationVm: tick remaining=${tick.remainingSeconds} fin=${tick.isFinished}"
                )
                if (tick.remainingSeconds == 0 && uiState.value.selectedMinutes == 0) {
                    Timber.d("StopwatchDurationVm: tick skipped — no duration selected yet")
                    return@collect
                }
                val mins = tick.remainingSeconds / SECONDS_IN_MINUTE
                val secs = tick.remainingSeconds % SECONDS_IN_MINUTE
                uiState.updateState {
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
    }

    private fun observeFinished() {
        Timber.d("StopwatchDurationVm: observeFinished started")
        viewModelScope.launch(getDispatcherIo()) {
            stopwatchOperationUseCase.onFinished.collect {
                Timber.d("StopwatchDurationVm: timer finished event received")
                uiState.updateState {
                    copy(
                        isTimerFinished = true,
                        formattedTime = "00:00",
                    )
                }
            }
        }
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
    }
}
