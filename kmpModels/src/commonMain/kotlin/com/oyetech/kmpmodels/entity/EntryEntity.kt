package com.oyetech.kmpmodels.entity

import kotlin.time.Instant

data class EntryEntity(
    val id: String,
    val areaId: String,
    val text: String,
    val createdBy: String = "",
    val note: String? = null,
    val occurredAt: Instant? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val durationSeconds: Long? = null,
    val timerType: TimerTypeEntry? = null,
    val startedAt: Instant? = null,
    val endedAt: Instant? = null,
)
