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

data class StopwatchRecord(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationMinutes: Int,
    val status: StopwatchRecordStatus,
)

