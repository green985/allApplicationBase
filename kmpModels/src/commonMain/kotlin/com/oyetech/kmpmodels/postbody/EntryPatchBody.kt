package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class EntryPatchBody(
    val areaId: String? = null,
    val text: String? = null,
    val note: String? = null,
    val occurredAt: String? = null,
    val durationSeconds: Long? = null,
    val timerType: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
)
