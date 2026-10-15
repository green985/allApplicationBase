package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class EntryDurationClearPostBody(
    val durationSeconds: Long?,
)
