package com.oyetech.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.RadioModelDao
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.utils.const.HelperConstant

@Database(
    entities = [RadioStationResponseData::class],
    version = HelperConstant.RadioModelVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun radioModelDao(): RadioModelDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseee: RadioDatabase

        fun buildDatabase(context: Context): RadioDatabase {

            radioDatabaseee = Room.databaseBuilder(
                context.applicationContext,
                RadioDatabase::class.java,
                "NacAppRadio.db"
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
