package com.oyetech.composebase.baseViews.bottomNavigation

import kotlinx.coroutines.flow.MutableStateFlow

class BottomNavigationDelegate {

    val bottomNavigationVisibilityState = MutableStateFlow(true)

    fun triggerBottomNavigationVisibilityState(
        isVisible: Boolean,
    ) {
        bottomNavigationVisibilityState.value = isVisible
    }
}