package com.oyetech.composebase.sharedScreens.allScreenNavigator

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent.NavigateListItemClicked
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent.OnNavigateToQuoteStart
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent.OnNavigateToRadioStart
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-18.01.2025-
-11:07-
 **/

class AllScreenNavigatorVM(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(AllScreenNavigatorUiState())

    init {
    }

    override fun onEvent(event: Any) {
        if (event is AllScreenNavigatorEvent) {
            when (event) {
                is OnNavigateToRadioStart -> {
                    // Handle idle event if needed
                    navigationUseCase.navigate(AllScreenNavigator.radioStart)
                }

                is OnNavigateToQuoteStart -> {
                    // Handle idle event if needed
                    navigationUseCase.navigate(AllScreenNavigator.quoteStart)
                }

                is NavigateListItemClicked -> {
                    // Handle item click event
                    navigationUseCase.navigate(event.navigationRoute)
                }
            }
        }
    }
}