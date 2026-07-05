package com.oyetech.local.di

import android.content.Context
import androidx.room.Room
import com.oyetech.local.database.AppDatabase
import org.koin.dsl.module

object LocalDatabaseModule {
    val module = module {
        single {
            Room.databaseBuilder(
                get<Context>(),
                AppDatabase::class.java,
                "app_database"
            )
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
        }
        single { get<AppDatabase>().stopwatchRecordDao() }
    }
}
