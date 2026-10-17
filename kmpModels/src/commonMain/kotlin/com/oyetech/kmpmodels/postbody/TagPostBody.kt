package com.oyetech.kmpmodels.postbody

import kotlinx.serialization.Serializable

@Serializable
data class TagPostBody(
    val areaId: String,
    val name: String,
)
