package com.oyetech.repository.stopwatch

import com.oyetech.dao.StopwatchRecordDao
import com.oyetech.domain.repository.stopwatch.StopwatchRecord
import com.oyetech.domain.repository.stopwatch.StopwatchRecordRepository
import com.oyetech.domain.repository.stopwatch.StopwatchRecordStatus
import com.oyetech.domain.repository.stopwatch.StopwatchTag
import com.oyetech.local.entity.StopwatchRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StopwatchRecordRepositoryImpl(
    private val dao: StopwatchRecordDao,
) : StopwatchRecordRepository {

    override suspend fun insertRecord(
        startedAt: Long,
        endedAt: Long,
        durationMinutes: Int,
        status: StopwatchRecordStatus,
        tag: StopwatchTag?,
    ) {
        dao.insert(
            StopwatchRecordEntity(
                startedAt = startedAt,
                endedAt = endedAt,
                durationMinutes = durationMinutes,
                status = status.name,
                tag = tag?.name,
            )
        )
    }

    override fun getAll(): Flow<List<StopwatchRecord>> {
        return dao.getAll().map { list ->
            list.map { entity ->
                StopwatchRecord(
                    id = entity.id,
                    startedAt = entity.startedAt,
                    endedAt = entity.endedAt,
                    durationMinutes = entity.durationMinutes,
                    status = StopwatchRecordStatus.valueOf(entity.status),
                    tag = entity.tag?.let { runCatching { StopwatchTag.valueOf(it) }.getOrNull() },
                )
            }
        }
    }
}

