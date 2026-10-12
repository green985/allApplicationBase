package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class EntryPostBody(
    val areaId: String? = null,
    val text: String = "",
    val entryDate: String,
    val note: String? = null,
    val durationSeconds: Long? = null,
    val markAsCompleted: Boolean = false,
)
