package com.oyetech.local.database.messaging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oyetech.dao.firebaseMessaging.MessagesAllDao
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatusTypeConverter
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-25.02.2025-
-23:16-
 **/

@Database(
    entities = [FirebaseMessagingLocalData::class],
    version = HelperConstant.FirebaseMessagingVersion,
    exportSchema = false
)
@TypeConverters(MessageStatusTypeConverter::class)
abstract class MessagesAllDatabase : RoomDatabase() {

    abstract fun modelDao(): MessagesAllDao

    companion object {

        lateinit var databasee: MessagesAllDatabase

        fun buildDatabase(context: Context): MessagesAllDatabase {

            databasee = Room.databaseBuilder(
                context.applicationContext,
                MessagesAllDatabase::class.java,
                "MessagesAllDatabase.db"
            )
                .fallbackToDestructiveMigration()
                .build()
            return databasee
        }

        fun clearAllTable() {
            databasee.clearAllTables()
        }
    }
}
