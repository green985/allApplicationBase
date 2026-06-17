package com.oyetech.domain.useCases

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

data class StopwatchTickResult(
    val remainingSeconds: Int = 0,
    val isFinished: Boolean = false,
)

class StopwatchOperationUseCase {

    private val _tickState = MutableStateFlow(StopwatchTickResult())
    val tickState: StateFlow<StopwatchTickResult> = _tickState.asStateFlow()

    private val _onFinished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onFinished = _onFinished.asSharedFlow()

    // Stored for resume calculation
    var startEpochMs: Long = 0L
        private set
    var totalSeconds: Int = 0
        private set

    fun startCountdown(minutes: Int): Flow<StopwatchTickResult> {
        totalSeconds = minutes * 60
        startEpochMs = System.currentTimeMillis()
        Timber.d("StopwatchOperationUseCase: startCountdown minutes=$minutes totalSeconds=$totalSeconds startEpochMs=$startEpochMs")
        return flow {
            for (remaining in totalSeconds downTo 0) {
                val result = StopwatchTickResult(
                    remainingSeconds = remaining,
                    isFinished = remaining == 0,
                )
                Timber.d("StopwatchOperationUseCase: emit remainingSeconds=$remaining isFinished=${result.isFinished}")
                _tickState.value = result
                emit(result)
                if (remaining == 0) {
                    Timber.d("StopwatchOperationUseCase: emitting onFinished")
                    _onFinished.tryEmit(Unit)
                }
                if (remaining > 0) delay(1000L)
            }
            Timber.d("StopwatchOperationUseCase: flow completed")
        }
    }

    /** Calculates remaining seconds based on wall-clock time. Used by Service on resume. */
    fun remainingSecondsFromWallClock(): Int {
        if (startEpochMs == 0L || totalSeconds == 0) return 0
        val elapsedSeconds = ((System.currentTimeMillis() - startEpochMs) / 1000L).toInt()
        return maxOf(0, totalSeconds - elapsedSeconds)
    }
}
