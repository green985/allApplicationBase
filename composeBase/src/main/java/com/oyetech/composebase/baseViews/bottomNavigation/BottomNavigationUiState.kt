package com.oyetech.composebase.baseViews.bottomNavigation

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

/**
Created by Erdi Özbek
-15.07.2025-
-00:12-
 **/

data class BottomNavigationUiState(val asdasd: Boolean = false) : BaseUIState()

sealed class BottomNavigationEvent : BaseEvent() {
    data class NavigateToSelectedItem(val index: Int) : BottomNavigationEvent()
}

sealed class BottomNavigationUiEvent : BaseEvent() {
    data class NavigateToSelectedItemWithTest(val index: Int) : BottomNavigationUiEvent()
}