package com.oyetech.local.database.messaging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oyetech.dao.firebaseMessaging.FirebaseMessagingDao
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import com.oyetech.models.firebaseModels.messagingModels.MessageStatusTypeConverter
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-20.02.2025-
-20:44-
 **/

@Database(
    entities = [FirebaseMessagingLocalData::class],
    version = HelperConstant.FirebaseMessagingVersion,
    exportSchema = false
)
@TypeConverters(MessageStatusTypeConverter::class)
abstract class FirebaseMessagingMessageDatabase : RoomDatabase() {

    abstract fun radioModelDao(): FirebaseMessagingDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var radioDatabaseee: FirebaseMessagingMessageDatabase

        fun buildDatabase(context: Context): FirebaseMessagingMessageDatabase {

            radioDatabaseee = Room.databaseBuilder(
                context.applicationContext,
                FirebaseMessagingMessageDatabase::class.java,
                "FirebaseMessagingSending.db"
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
