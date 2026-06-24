package com.oyetech.domain.repository.stopwatch

import kotlinx.coroutines.flow.Flow

interface StopwatchRecordRepository {
    suspend fun insertRecord(
        startedAt: Long,
        endedAt: Long,
        durationMinutes: Int,
        status: StopwatchRecordStatus,
    )

    fun getAll(): Flow<List<StopwatchRecord>>
}

enum class StopwatchRecordStatus { FINISHED, CANCELLED }

/**
 * Model passed when starting a stopwatch session.
 * Duration is expressed in seconds so both second- and minute-based selections are supported.
 * Future fields such as note or task can be added here without changing the start signature.
 */
data class StopwatchSession(
    val durationSeconds: Int,
)

data class StopwatchRecord(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationMinutes: Int,
    val status: StopwatchRecordStatus,
)

