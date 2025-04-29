package com.oyetech.composebase.sharedScreens.allScreenNavigator

/**
Created by Erdi Özbek
-18.01.2025-
-11:08-
 **/

data class AllScreenNavigatorUiState(val isLoading: Boolean = false)

sealed class AllScreenNavigatorEvent {
    data class Idle(val data: Int) : AllScreenNavigatorEvent()
    object Idlee : AllScreenNavigatorEvent()
}