package com.oyetech.kmpmodels.entity

import kotlin.time.Instant

data class EntryEntity(
    val id: String,
    val areaId: String,
    val text: String,
    val entryDate: String? = null,
    val durationSeconds: Long? = null,
    val timerStartedAt: Instant? = null,
    val timerEndsAt: Instant? = null,
    val timerCancelledAt: Instant? = null,
    val completedAt: Instant? = null,
    val createdBy: String = "",
    val note: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val timerType: TimerTypeEntry? = null,
    val startedAt: Instant? = null,
    val endedAt: Instant? = null,
)
