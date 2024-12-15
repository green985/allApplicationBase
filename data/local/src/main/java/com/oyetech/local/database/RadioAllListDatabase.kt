package com.oyetech.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.RadioAllListDao
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.utils.const.HelperConstant

@Database(
    entities = [RadioStationResponseData::class],
    version = HelperConstant.RadioAllListVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class RadioAllListDatabase : RoomDatabase() {

    abstract fun radioModelDao(): RadioAllListDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseee: RadioAllListDatabase

        fun buildDatabase(context: Context): RadioAllListDatabase {

            radioDatabaseee = Room.databaseBuilder(
                context.applicationContext,
                RadioAllListDatabase::class.java,
                "RadioAllList.db"
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
