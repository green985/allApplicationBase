package com.oyetech.composebase.baseViews.bottomNavigation

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-15.07.2025-
-00:12-
 **/

sealed class BottomNavigationEvent : BaseEvent() {
    data class NavigateToSelectedItem(val index: Int) : BottomNavigationEvent()
}

sealed class BottomNavigationUiEvent : BaseEvent() {
    data class NavigateToSelectedItemWithTest(val index: Int) : BottomNavigationUiEvent()
}
