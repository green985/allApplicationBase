package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class DiaryBulkPostBody(
    val requestId: String,
    val entries: List<DiaryBulkEntryPostBody>,
)

@Serializable
data class DiaryBulkEntryPostBody(
    val entryDate: String,
    val areaId: String? = null,
    val text: String = "",
    val tagId: String? = null,
    val durationSeconds: Long? = null,
    val markAsCompleted: Boolean = false,
)
