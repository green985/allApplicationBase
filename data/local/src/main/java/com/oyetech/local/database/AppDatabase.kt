package com.oyetech.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE stopwatch_records ADD COLUMN tag TEXT")
            }
        }
    }
}

