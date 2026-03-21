package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QueOption(
    val id: String = "",
    val text: String = "",
    val value: Double? = null,
    val order: Int = 0,
)
