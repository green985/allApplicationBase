package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: String,
    val name: String,
)
