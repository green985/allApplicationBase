package com.oyetech.quotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.models.quotes.responseModel.TagsTypeConverter
import com.oyetech.quotes.dao.QuotesAllListDao

/**
Created by Erdi Özbek
-18.12.2024-
-02:24-
 **/

@Database(
    entities = [QuoteResponseData::class],
    version = QuoteAllListDatabase.databaseVersion,
    exportSchema = false
)

// @TypeConverters(UserPropertyDataResponseConverter::class)
@TypeConverters(TagsTypeConverter::class)
abstract class QuoteAllListDatabase : RoomDatabase() {

    abstract fun quotesAllListDao(): QuotesAllListDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {
        const val databaseVersion = 4
        lateinit var database: QuoteAllListDatabase

        fun buildDatabase(context: Context): QuoteAllListDatabase {

            database = Room.databaseBuilder(
                context.applicationContext,
                QuoteAllListDatabase::class.java,
                "QuoteAllList.db"
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
