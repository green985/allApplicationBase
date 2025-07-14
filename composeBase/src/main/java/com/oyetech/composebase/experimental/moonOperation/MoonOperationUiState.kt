package com.oyetech.composebase.experimental.moonOperation

/**
Created by Erdi Özbek
-8.07.2025-
-00:13-
 **/
data class MoonOperationUiState(
    val phaseName: String = "",
    val moonName: String = "",
    val illuminationPercent: Int? = null,
    val age: Double? = null,
    val distanceToMoon: Int? = null,
    val distanceToSun: Int? = null,
    val error: String? = null,
)

sealed class MoonOperationEvent {
    object Moon : MoonOperationEvent()
}