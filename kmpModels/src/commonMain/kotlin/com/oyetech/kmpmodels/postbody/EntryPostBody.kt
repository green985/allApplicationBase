package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi

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
)
