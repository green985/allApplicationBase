package com.oyetech.composebase.baseViews.bottomNavigation;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent.Idle
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent.Idlee
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent.NavigateToSelectedItem
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-15.07.2025-
-00:11-
 **/

class BottomNavigationVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(BottomNavigationUiState())

    val uiEvent = MutableSharedFlow<BottomNavigationUiEvent>()

    var selectedItem = MutableStateFlow<Int>(0)

    init {
    }

    override fun onEvent(event: Any) {
        if (event is BottomNavigationEvent) {

            when (event) {
                is Idle -> TODO()
                Idlee -> TODO()
                is NavigateToSelectedItem -> {
                    selectedItem.value = event.index
                    viewModelScope.launch {
                        uiEvent.emit(BottomNavigationUiEvent.NavigateToSelectedItemWithTest(event.index))
                    }
                }
            }
        }
    }
}