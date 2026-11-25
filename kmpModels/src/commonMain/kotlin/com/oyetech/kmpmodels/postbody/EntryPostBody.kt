package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

const val MAX_ENTRY_TEXT_LENGTH = 10_000

@Serializable
data class EntryPostBody(
    val areaId: String?,
    val text: String? = null,
    val entryDate: String? = null,
    val note: String? = null,
    val durationSeconds: Long? = null,
    val markAsCompleted: Boolean? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    val tagId: String? = null,
    val isReminder: Boolean? = null,
)
