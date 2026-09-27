package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class EntryPostBody(
    val areaId: String,
    val text: String,
    val entryDate: String,
    val note: String? = null,
    val durationSeconds: Long? = null,
    val timerType: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
)
