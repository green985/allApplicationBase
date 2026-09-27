package com.oyetech.kmpmodels.response

import kotlinx.serialization.Serializable

@Serializable
data class GenericResponse<T>(
    val data: T? = null,
    val message: String = "",
    val status: Boolean = false,
)
