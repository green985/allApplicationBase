package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class EntryResponse(
    val id: String,
    val areaId: String,
    val text: String,
    val createdBy: String? = null,
    val note: String? = null,
    val occurredAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val durationSeconds: Long? = null,
    val timerType: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
)
