package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class DiaryBulkEntryResponse(
    val id: String,
    val entryDate: String,
    val areaId: String,
    val text: String,
    val createdAt: String,
)
