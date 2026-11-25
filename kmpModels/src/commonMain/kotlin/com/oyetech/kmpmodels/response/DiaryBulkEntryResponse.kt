package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class DiaryBulkEntryResponse(
    val id: String,
    val entryDate: String,
    val areaId: String? = null,
    val text: String,
    val createdAt: String,
    val tag: TagResponse? = null,
    val durationSeconds: Long? = null,
    val completedAt: String? = null,
)
