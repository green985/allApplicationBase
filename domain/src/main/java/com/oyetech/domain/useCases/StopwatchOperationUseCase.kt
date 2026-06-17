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

    private var startEpochMs: Long = 0L
    private var totalSeconds: Int = 0

    fun hasActiveSession(): Boolean {
        val active = startEpochMs > 0L && remainingSecondsFromWallClock() > 0
        Timber.d("StopwatchOperationUseCase: hasActiveSession=$active")
        return active
    }

    fun startCountdown(minutes: Int): Flow<StopwatchTickResult> {
        totalSeconds = minutes * 60
        startEpochMs = System.currentTimeMillis()
        Timber.d("StopwatchOperationUseCase: startCountdown minutes=$minutes totalSeconds=$totalSeconds")
        return buildFlow(totalSeconds)
    }

    fun resumeCountdown(): Flow<StopwatchTickResult> {
        val remaining = remainingSecondsFromWallClock()
        Timber.d("StopwatchOperationUseCase: resumeCountdown remaining=$remaining")
        return buildFlow(remaining)
    }

    private fun remainingSecondsFromWallClock(): Int {
        if (startEpochMs == 0L || totalSeconds == 0) return 0
        val elapsed = ((System.currentTimeMillis() - startEpochMs) / 1000L).toInt()
        return maxOf(0, totalSeconds - elapsed)
    }

    private fun buildFlow(fromSeconds: Int): Flow<StopwatchTickResult> = flow {
        for (remaining in fromSeconds downTo 0) {
            val result = StopwatchTickResult(
                remainingSeconds = remaining,
                isFinished = remaining == 0,
            )
            Timber.d("StopwatchOperationUseCase: emit remaining=$remaining isFinished=${result.isFinished}")
            _tickState.value = result
            emit(result)
            if (remaining == 0) {
                Timber.d("StopwatchOperationUseCase: emitting onFinished")
                _onFinished.tryEmit(Unit)
            }
            if (remaining > 0) delay(TICK_MS)
        }
        Timber.d("StopwatchOperationUseCase: flow completed")
    }

    companion object {
        private const val TICK_MS = 1000L
    }
}
