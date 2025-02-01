package com.oyetech.quotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oyetech.models.quotes.responseModel.AdviceQuoteResponseData
import com.oyetech.models.quotes.responseModel.TagsTypeConverter
import com.oyetech.quotes.dao.AdviceQuoteListDao

@Database(
    entities = [AdviceQuoteResponseData::class],
    version = QuoteAllListDatabase.databaseVersion,
    exportSchema = false
)

// @TypeConverters(UserPropertyDataResponseConverter::class)
@TypeConverters(TagsTypeConverter::class)
abstract class AdviceQuoteListDatabase : RoomDatabase() {

    abstract fun listDao(): AdviceQuoteListDao

    companion object {
        lateinit var database: AdviceQuoteListDatabase

        fun buildDatabase(context: Context): AdviceQuoteListDatabase {

            database = Room.databaseBuilder(
                context.applicationContext,
                AdviceQuoteListDatabase::class.java,
                "AdviceQuoteList.db"
            )
                .fallbackToDestructiveMigration()
                .build()
            return database
        }

        fun clearAllTable() {
            database.clearAllTables()
        }
    }
}
