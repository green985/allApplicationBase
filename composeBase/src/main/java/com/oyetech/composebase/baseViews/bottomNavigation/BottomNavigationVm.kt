package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent.NavigateToSelectedItem
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-15.07.2025-
-00:11-
 **/

class BottomNavigationVm(
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val uiEvent = MutableSharedFlow<BottomNavigationUiEvent>()

    override fun onEvent(event: Any) {
        if (event is BottomNavigationEvent) {
            when (event) {
                is NavigateToSelectedItem -> {
                    viewModelScope.launch {
                        uiEvent.emit(BottomNavigationUiEvent.NavigateToSelectedItemWithTest(event.index))
                    }
                }
            }
        }
    }
}
