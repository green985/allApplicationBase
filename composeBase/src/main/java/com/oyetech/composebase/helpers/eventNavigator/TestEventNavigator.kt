package com.oyetech.composebase.helpers.eventNavigator

import androidx.lifecycle.LifecycleCoroutineScope
import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactUIEvent
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent
import com.oyetech.composebase.sharedScreens.settings.FacSettingsUiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-1.07.2025-
-00:03-
 **/

class TestEventNavigator {

    private val delayMillis: Long = 3000 // Delay in milliseconds between events
    private val initialDelayMillis: Long = 3000 // Initial delay before starting to emit events

    private val _eventFlow =
        MutableSharedFlow<BaseEvent>(replay = 0)
    val eventFlow: SharedFlow<BaseEvent> = _eventFlow

    suspend fun triggerEvents(events: List<BaseEvent>) {
        delay(initialDelayMillis)
        for (event in events) {
            _eventFlow.emit(event)
            delay(delayMillis)
        }
    }

    fun triggerTestEvents(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch(Dispatchers.IO) {
            triggerEvents(EventNavigatorList.contactWithUsFullScopeOperation)
        }

    }

    companion object {
        fun bottomNavigationTest(): List<BaseEvent> {
            return buildList {
                add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
                add(BottomNavigationEvent.NavigateToSelectedItem(1))
            }
        }

        fun getDummyEventList(): List<BaseEvent> {
            return buildList {
                add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
                add(BottomNavigationEvent.NavigateToSelectedItem(1))
            }
        }

        fun getDummyEvent(): BaseEvent {
            return object : BaseEvent() {}
        }
    }
}

object EventNavigatorList {

    val cancelUserRegistrationOperation = buildList<BaseEvent> {
        add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
        add(QuoteListUiEvent.QuoteListItemClicked(1))
        add(LoginOperationEvent.LoginClicked)
        add(LoginOperationEvent.OnCancel)
    }

    val contactWithUsFullScopeOperation = buildList<BaseEvent> {
        add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
        add(BottomNavigationEvent.NavigateToSelectedItem(1))
        add(FacSettingsUiEvent.ContactClicked)
        add(ContactUIEvent.UpdateName("Test User"))
        add(ContactUIEvent.UpdateMessage("Test"))
        add(ContactUIEvent.UpdateMessage(""))
        add(ContactUIEvent.UpdateMessage("Test"))
        add(ContactUIEvent.Submit)

    }

}