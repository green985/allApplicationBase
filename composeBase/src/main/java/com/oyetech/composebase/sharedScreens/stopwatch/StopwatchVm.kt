package com.oyetech.composebase.sharedScreens.stopwatch

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.repository.stopwatch.StopwatchTag
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class StopwatchVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val stopwatchOperationUseCase: StopwatchOperationUseCase,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(StopwatchUiState())

    init {
        Timber.d("StopwatchVm: init")
        observeTickState()
    }

    override fun onEvent(event: Any) {
        Timber.d("StopwatchVm: onEvent -> $event")
        if (event is StopwatchEvent) {
            when (event) {
                StopwatchEvent.OnCancelClicked -> onCancel()
                StopwatchEvent.OnFinishClicked -> onFinish()
                is StopwatchEvent.OnTagSelected -> onTagSelected(event.tag)
            }
        }
    }

    private fun onTagSelected(tag: StopwatchTag) {
        val current = uiState.value.selectedTag
        val next = if (current == tag) null else tag
        Timber.d("StopwatchVm: onTagSelected tag=$tag next=$next")
        stopwatchOperationUseCase.updateSessionTag(next)
        uiState.updateState { copy(selectedTag = next) }
    }

    private fun observeTickState() {
        viewModelScope.launch(getDispatcherIo()) {
            stopwatchOperationUseCase.tickState.collect { tick ->
                Timber.d(
                    "StopwatchVm: tick remaining=${tick.remainingSeconds} " +
                        "fin=${tick.isFinished} cancelled=${tick.isCancelled}"
                )
                if (tick.isCancelled) return@collect
                if (tick.isFinished) {
                    uiState.updateState { copy(isFinished = true) }
                }
                if (tick.remainingSeconds > 0) {
                    val tags = stopwatchOperationUseCase.currentSuggestedTags()
                    if (uiState.value.suggestedTags != tags) {
                        Timber.d("StopwatchVm: suggestedTags changed — updating tags=$tags")
                        uiState.updateState { copy(suggestedTags = tags, selectedTag = null) }
                    }
                }
                val mins = tick.remainingSeconds / SECONDS_IN_MINUTE
                val secs = tick.remainingSeconds % SECONDS_IN_MINUTE
                uiState.updateState {
                    copy(
                        formattedTime = "${mins.toString().padStart(2, '0')}:" +
                            secs.toString().padStart(2, '0'),
                    )
                }
            }
        }
    }

    private fun onFinish() {
        Timber.d("StopwatchVm: onFinish")
        stopwatchOperationUseCase.clearFinishedPendingDisplay()
        navigationUseCase.goBack()
    }

    private fun onCancel() {
        Timber.d("StopwatchVm: onCancel")
        stopwatchOperationUseCase.clearFinishedPendingDisplay()
        stopwatchOperationUseCase.cancelCountdown()
        navigationUseCase.goBack()
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
    }
}

