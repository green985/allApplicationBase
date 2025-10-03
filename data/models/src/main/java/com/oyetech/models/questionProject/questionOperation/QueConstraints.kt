package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QueConstraints(
    val required: Boolean? = null,
    val minSelections: Int? = null,
    val maxSelections: Int? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val step: Double? = null,
)