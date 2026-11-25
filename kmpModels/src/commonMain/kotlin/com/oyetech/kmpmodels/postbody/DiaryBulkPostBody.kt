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
    val areaId: String,
    val text: String,
)
