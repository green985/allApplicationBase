package com.oyetech.domain.repository.stopwatch

import kotlinx.coroutines.flow.Flow

interface StopwatchRecordRepository {
    suspend fun insertRecord(
        startedAt: Long,
        endedAt: Long,
        durationMinutes: Int,
        status: StopwatchRecordStatus,
        tag: StopwatchTag? = null,
    )

    fun getAll(): Flow<List<StopwatchRecord>>
}

enum class StopwatchRecordStatus { FINISHED, CANCELLED }

enum class StopwatchTag {
    KAHVALTI,
    SIGARA,
    MEDITASYON,
    YEMEK_HAZIRLAMA,
    YEMEK_YEME,
}

/**
 * Model passed when starting a stopwatch session.
 * Duration is expressed in seconds so both second- and minute-based selections are supported.
 * Future fields such as note or task can be added here without changing the start signature.
 */
data class StopwatchSession(
    val durationSeconds: Int,
    val tag: StopwatchTag? = null,
)

data class StopwatchRecord(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationMinutes: Int,
    val status: StopwatchRecordStatus,
    val tag: StopwatchTag? = null,
)

