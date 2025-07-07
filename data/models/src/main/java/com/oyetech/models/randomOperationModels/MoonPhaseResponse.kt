package com.oyetech.models.randomOperationModels

data class MoonPhaseResponse(
    val error: String?,
    val targetDate: String,
    val index: Int,
    val phase: String,
    val illumination: String,
    val moonrise: String?,
    val moonset: String?,
)