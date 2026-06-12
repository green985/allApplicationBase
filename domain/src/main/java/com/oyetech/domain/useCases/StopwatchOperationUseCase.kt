package com.oyetech.domain.useCases

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class StopwatchTickResult(
    val remainingSeconds: Int = 0,
)

class StopwatchOperationUseCase {

    fun startCountdown(minutes: Int): Flow<StopwatchTickResult> = flow {
        val totalSeconds = minutes * 60
        for (remaining in totalSeconds downTo 0) {
            emit(StopwatchTickResult(remainingSeconds = remaining))
            if (remaining > 0) delay(1000L)
        }
    }
}

