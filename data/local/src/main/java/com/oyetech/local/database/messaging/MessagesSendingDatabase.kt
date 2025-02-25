package com.oyetech.local.database.messaging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oyetech.dao.firebaseMessaging.MessagesSendingDao
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
abstract class MessagesSendingDatabase : RoomDatabase() {

    abstract fun radioModelDao(): MessagesSendingDao
    // abstract fun messageConversationDao(): MessagesConversationDao

    companion object {

        lateinit var databasee: MessagesSendingDatabase

        fun buildDatabase(context: Context): MessagesSendingDatabase {

            databasee = Room.databaseBuilder(
                context.applicationContext,
                MessagesSendingDatabase::class.java,
                "MessagesSending.db"
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
