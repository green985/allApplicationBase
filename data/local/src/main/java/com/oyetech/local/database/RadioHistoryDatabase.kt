package com.oyetech.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.RadioHistoryDao
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-24.11.2022-
-01:40-
 **/

@Database(
    entities = [RadioStationResponseData::class],
    version = HelperConstant.HistoryDatabaseVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class RadioHistoryDatabase : RoomDatabase() {

    abstract fun radioModelDao(): RadioHistoryDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseee: RadioHistoryDatabase

        fun buildDatabase(context: Context): RadioHistoryDatabase {

            radioDatabaseee = Room.databaseBuilder(
                context.applicationContext,
                RadioHistoryDatabase::class.java,
                "NacAppHistory.db"
            )
                .fallbackToDestructiveMigration()
                .build()
            return radioDatabaseee
        }

        fun clearAllTable() {
            radioDatabaseee.clearAllTables()
        }
    }
}
