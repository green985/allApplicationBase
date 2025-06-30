package com.oyetech.composebase.sharedScreens.allScreenNavigator

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-18.01.2025-
-11:08-
 **/

data class AllScreenNavigatorUiState(val isLoading: Boolean = false)

sealed class AllScreenNavigatorEvent : BaseEvent() {
    data class NavigateListItemClicked(val navigationRoute: String) : AllScreenNavigatorEvent()

    object OnNavigateToRadioStart : AllScreenNavigatorEvent()
    object OnNavigateToQuoteStart : AllScreenNavigatorEvent()
}