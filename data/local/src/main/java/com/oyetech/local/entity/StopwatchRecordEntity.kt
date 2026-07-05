package com.oyetech.local.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "stopwatch_records")
data class StopwatchRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,        // epoch ms
    val endedAt: Long,          // epoch ms
    val durationSeconds: Int,
    val status: String,         // "FINISHED" | "CANCELLED"
    val tag: String? = null,    // StopwatchTag name or null
)

