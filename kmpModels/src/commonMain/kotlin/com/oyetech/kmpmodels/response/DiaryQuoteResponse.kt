package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class DiaryQuoteResponse(
    val date: String,
    val quote: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class DiaryArchiveResponse(
    val entries: List<EntryResponse>,
    val quotes: List<DiaryQuoteResponse>,
)
