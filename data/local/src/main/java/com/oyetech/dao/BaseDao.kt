package com.oyetech.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
Created by Erdi Özbek
-23.11.2022-
-19:24-
 **/
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Update
    fun update(item: T): Int
}
