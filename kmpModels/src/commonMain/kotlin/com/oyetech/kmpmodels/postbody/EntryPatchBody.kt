package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class EntryPatchBody(
    val areaId: String? = null,
    val text: String? = null,
    val note: String? = null,
    val entryDate: String? = null,
    val durationSeconds: Long? = null,
    val markAsCompleted: Boolean = false,
)
