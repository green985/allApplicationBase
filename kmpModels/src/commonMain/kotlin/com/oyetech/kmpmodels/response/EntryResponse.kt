package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class EntryResponse(
    val id: String,
    val areaId: String? = null,
    val text: String,
    val entryDate: String,
    val createdBy: String? = null,
    val note: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val durationSeconds: Long? = null,
    val timerStartedAt: String? = null,
    val timerEndsAt: String? = null,
    val timerCancelledAt: String? = null,
    val completedAt: String? = null,
)
