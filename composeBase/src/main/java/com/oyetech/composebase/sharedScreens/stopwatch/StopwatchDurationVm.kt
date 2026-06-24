package com.oyetech.composebase.sharedScreens.stopwatch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.repository.stopwatch.StopwatchSession
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class StopwatchDurationVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val stopwatchOperationUseCase: StopwatchOperationUseCase,
    private val appContext: Context,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(StopwatchDurationUiState())

    init {
        Timber.d("StopwatchDurationVm: init")
        buildDurations()
        if (stopwatchOperationUseCase.hasActiveSession() || stopwatchOperationUseCase.isFinishedPendingDisplay) {
            Timber.d("StopwatchDurationVm: active or finished session found — navigating to StopwatchScreen")
            navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
        }
        observeTickState()
        observeFinished()
    }

    private fun buildDurations() {
        val items = DURATION_SECONDS.map { seconds ->
            StopwatchDurationItem(
                session = StopwatchSession(durationSeconds = seconds),
                label = formatDurationLabel(seconds),
            )
        }
        uiState.updateState { copy(durations = items) }
    }

    private fun formatDurationLabel(durationSeconds: Int): String {
        return if (durationSeconds % SECONDS_IN_MINUTE == 0) {
            "${durationSeconds / SECONDS_IN_MINUTE} min"
        } else {
            "$durationSeconds sec"
        }
    }

    override fun onEvent(event: Any) {
        Timber.d("StopwatchDurationVm: onEvent -> $event")
        if (event is StopwatchDurationEvent) {
            when (event) {
                is StopwatchDurationEvent.OnDurationSelected -> onDurationSelected(event.session)
            }
        }
    }

    private fun onDurationSelected(session: StopwatchSession) {
        Timber.d("StopwatchDurationVm: onDurationSelected durationSeconds=${session.durationSeconds}")
        uiState.updateState { copy(selectedDurationSeconds = session.durationSeconds) }
        startTimerService(session)
        navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
    }

    @SuppressLint("NewApi")
    private fun startTimerService(session: StopwatchSession) {
        Timber.d("StopwatchDurationVm: startTimerService durationSeconds=${session.durationSeconds} packageName=${appContext.packageName}")
        val intent = Intent("com.oyetech.wear.ACTION_START_TIMER").apply {
            setPackage(appContext.packageName)
            putExtra("seconds", session.durationSeconds)
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
                if (tick.remainingSeconds == 0 && uiState.value.selectedDurationSeconds == 0) {
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
                Timber.d("StopwatchDurationVm: timer finished event received — navigating to StopwatchScreen")
                uiState.updateState {
                    copy(
                        isTimerFinished = true,
                        formattedTime = "00:00",
                    )
                }
                navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
            }
        }
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
        private val DURATION_SECONDS = listOf(10, 60, 5 * 60, 10 * 60, 15 * 60)
    }
}
