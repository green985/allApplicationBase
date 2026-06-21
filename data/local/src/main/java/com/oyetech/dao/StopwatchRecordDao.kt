package com.oyetech.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oyetech.local.entity.StopwatchRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StopwatchRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: StopwatchRecordEntity)

    @Query("SELECT * FROM stopwatch_records ORDER BY startedAt DESC")
    fun getAll(): Flow<List<StopwatchRecordEntity>>
}

