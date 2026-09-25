package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class DiaryQuotePutBody(
    val quote: String,
)
