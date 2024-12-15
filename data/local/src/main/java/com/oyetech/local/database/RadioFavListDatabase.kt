package com.oyetech.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.RadioFavListDao
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationFavModel
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-8.12.2022-
-22:38-
 **/

@Database(
    entities = [RadioStationFavModel::class],
    version = HelperConstant.HistoryDatabaseVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class RadioFavListDatabase : RoomDatabase() {

    abstract fun radioModelDao(): RadioFavListDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseee: RadioFavListDatabase

        fun buildDatabase(context: Context): RadioFavListDatabase {

            radioDatabaseee = Room.databaseBuilder(
                context.applicationContext,
                RadioFavListDatabase::class.java,
                "NacAppFavList.db"
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