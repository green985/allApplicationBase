package com.oyetech.local.database.contentDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oyetech.dao.contentDao.ContentLikeDao
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-11.03.2025-
-20:35-
 **/

@Database(
    entities = [LikeOperationModel::class],
    version = HelperConstant.ContentLikeVersion,
    exportSchema = false
)
// @TypeConverters(UserPropertyDataResponseConverter::class)
abstract class ContentLikeDatabase : RoomDatabase() {

    abstract fun modelDao(): ContentLikeDao

    companion object {

        lateinit var database: ContentLikeDatabase

        fun buildDatabase(context: Context): ContentLikeDatabase {

            database = Room.databaseBuilder(
                context.applicationContext,
                ContentLikeDatabase::class.java,
                "ContentLike.db"
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
