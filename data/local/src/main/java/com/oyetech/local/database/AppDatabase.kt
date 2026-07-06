package com.oyetech.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oyetech.dao.StopwatchRecordDao
import com.oyetech.local.entity.StopwatchRecordEntity

@Database(
    entities = [StopwatchRecordEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stopwatchRecordDao(): StopwatchRecordDao

    companion object {
    }
}

