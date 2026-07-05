package com.oyetech.domain.useCases

import com.oyetech.domain.repository.stopwatch.StopwatchRecordRepository
import com.oyetech.domain.repository.stopwatch.StopwatchRecordStatus
import com.oyetech.domain.repository.stopwatch.StopwatchSession
import com.oyetech.domain.repository.stopwatch.StopwatchTag
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
    val isCancelled: Boolean = false,
)

@Suppress("TooManyFunctions")
class StopwatchOperationUseCase(
    private val stopwatchRecordRepository: StopwatchRecordRepository,
) {

    private val _tickState = MutableStateFlow(StopwatchTickResult())
    val tickState: StateFlow<StopwatchTickResult> = _tickState.asStateFlow()

    private val _onFinished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onFinished = _onFinished.asSharedFlow()

    private var startEpochMs: Long = 0L
    private var totalSeconds: Int = 0
    private var durationMinutes: Int = 0
    private var isCancelRequested: Boolean = false
    private var sessionTag: StopwatchTag? = null
    var isFinishedPendingDisplay: Boolean = false
        private set

    fun hasActiveSession(): Boolean {
        val active = startEpochMs > 0L && remainingSecondsFromWallClock() > 0
        Timber.d("StopwatchOperationUseCase: hasActiveSession=$active")
        return active
    }

    fun markFinishedPendingDisplay() {
        Timber.d("StopwatchOperationUseCase: markFinishedPendingDisplay")
        isFinishedPendingDisplay = true
    }

    fun clearFinishedPendingDisplay() {
        Timber.d("StopwatchOperationUseCase: clearFinishedPendingDisplay")
        isFinishedPendingDisplay = false
    }

    fun cancelCountdown() {
        Timber.d("StopwatchOperationUseCase: cancelCountdown")
        isCancelRequested = true
    }

    fun startCountdown(session: StopwatchSession): Flow<StopwatchTickResult> {
        totalSeconds = session.durationSeconds
        durationMinutes = session.durationSeconds / SECONDS_IN_MINUTE
        startEpochMs = System.currentTimeMillis()
        sessionTag = session.tag
        Timber.d(
            "StopwatchOperationUseCase: startCountdown durationSeconds=${session.durationSeconds} " +
                "totalSeconds=$totalSeconds tag=$sessionTag"
        )
        return buildFlow(totalSeconds)
    }

    fun resumeCountdown(): Flow<StopwatchTickResult> {
        val remaining = remainingSecondsFromWallClock()
        Timber.d("StopwatchOperationUseCase: resumeCountdown remaining=$remaining")
        return buildFlow(remaining)
    }

    private fun remainingSecondsFromWallClock(): Int {
        if (startEpochMs == 0L || totalSeconds == 0) return 0
        val elapsed = ((System.currentTimeMillis() - startEpochMs) / MILLIS_IN_SECOND).toInt()
        return maxOf(0, totalSeconds - elapsed)
    }

    private fun buildFlow(fromSeconds: Int): Flow<StopwatchTickResult> = flow {
        isCancelRequested = false
        for (remaining in fromSeconds downTo 0) {
            if (isCancelRequested) {
                Timber.d("StopwatchOperationUseCase: cancel requested — recording and emitting isCancelled")
                val cancelled = StopwatchTickResult(isCancelled = true)
                _tickState.value = cancelled
                emit(cancelled)
                recordSession(StopwatchRecordStatus.CANCELLED)
                resetSession()
                break
            }
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
                recordSession(StopwatchRecordStatus.FINISHED)
                resetSession()
            }
            if (remaining > 0) delay(TICK_MS)
        }
        Timber.d("StopwatchOperationUseCase: flow completed")
    }

    private suspend fun recordSession(status: StopwatchRecordStatus) {
        val endedAt = System.currentTimeMillis()
        Timber.d(
            "StopwatchOperationUseCase: recordSession status=$status startedAt=$startEpochMs " +
                "endedAt=$endedAt tag=$sessionTag"
        )
        runCatching {
            stopwatchRecordRepository.insertRecord(
                startedAt = startEpochMs,
                endedAt = endedAt,
                durationMinutes = durationMinutes,
                status = status,
                tag = sessionTag,
            )
        }.onFailure { Timber.e(it, "StopwatchOperationUseCase: recordSession failed") }
    }

    private fun resetSession() {
        startEpochMs = 0L
        totalSeconds = 0
        durationMinutes = 0
        sessionTag = null
    }

    fun suggestedTagsFor(durationSeconds: Int): List<StopwatchTag> = when {
        durationSeconds == SECONDS_10 -> listOf(StopwatchTag.DENEME)
        durationSeconds / SECONDS_IN_MINUTE == MINUTES_5 -> listOf(StopwatchTag.KAHVALTI, StopwatchTag.SIGARA)
        durationSeconds / SECONDS_IN_MINUTE == MINUTES_10 -> listOf(
            StopwatchTag.MEDITASYON,
            StopwatchTag.YEMEK_HAZIRLAMA,
            StopwatchTag.YEMEK_YEME,
        )
        else -> emptyList()
    }

    fun currentSuggestedTags(): List<StopwatchTag> = suggestedTagsFor(totalSeconds)

    fun updateSessionTag(tag: StopwatchTag?) {
        Timber.d("StopwatchOperationUseCase: updateSessionTag tag=$tag")
        sessionTag = tag
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
        private const val MILLIS_IN_SECOND = 1000L
        private const val TICK_MS = 1000L
        private const val SECONDS_10 = 10
        private const val MINUTES_5 = 5
        private const val MINUTES_10 = 10
    }
}
