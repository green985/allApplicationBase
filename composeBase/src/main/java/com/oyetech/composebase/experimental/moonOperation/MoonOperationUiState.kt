package com.oyetech.composebase.experimental.moonOperation

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

/**
Created by Erdi Özbek
-8.07.2025-
-00:13-
 **/

data class MoonOperationUiState(val moonName: String) : BaseUIState()

sealed class MoonOperationEvent : BaseEvent() {
    object Moon : MoonOperationEvent()
}