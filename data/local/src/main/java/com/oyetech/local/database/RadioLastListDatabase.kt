package com.oyetech.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.RadioLastListDao
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-1.12.2022-
-16:25-
 **/

@Database(
    entities = [RadioStationResponseData::class],
    version = HelperConstant.RadioLastListDatabaseVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class RadioLastListDatabase : RoomDatabase() {

    abstract fun radioLastListDao(): RadioLastListDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseeee: RadioLastListDatabase

        fun buildDatabase(context: Context): RadioLastListDatabase {

            radioDatabaseeee = Room.databaseBuilder(
                context.applicationContext,
                RadioLastListDatabase::class.java,
                "NacAppLast.db"
            )
                .fallbackToDestructiveMigration()
                .build()
            return radioDatabaseeee
        }

        fun clearAllTable() {
            radioDatabaseeee.clearAllTables()
        }
    }
}
