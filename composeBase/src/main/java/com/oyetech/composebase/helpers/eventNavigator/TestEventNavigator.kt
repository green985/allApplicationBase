package com.oyetech.composebase.helpers.eventNavigator

import androidx.lifecycle.LifecycleCoroutineScope
import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent
import com.oyetech.composebase.sharedScreens.settings.FacSettingsUiEvent
import com.oyetech.composebase.sharedScreens.userList.UserListEvent
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
            triggerEvents(EventNavigatorList.userListStartConversationOperation)
        }

    }

    companion object {
        fun bottomNavigationTest(): List<BaseEvent> {
            return buildList {
                add(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
                add(BottomNavigationEvent.NavigateToSelectedItem(1))
            }
        }

        fun getDummyEventList(): List<BaseEvent> {
            return buildList {
                add(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
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
        add(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
        add(LoginOperationEvent.LoginClicked)
        add(LoginOperationEvent.OnCancel)
    }

    val contactWithUsFullScopeOperation = buildList<BaseEvent> {
//        add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
        add(BottomNavigationEvent.NavigateToSelectedItem(1))
        add(FacSettingsUiEvent.ContactClicked)
    }

    val userListStartConversationOperation = buildList<BaseEvent> {
        add(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
        add(BottomNavigationEvent.NavigateToSelectedItem(2))
        add(UserListEvent.OnUserClick(1))
    }

    // Navigate directly to Create Question screen in Question app
    val createQuestionOperation = buildList<BaseEvent> {
        add(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
        add(AllScreenNavigatorEvent.NavigateListItemClicked(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route))
    }

}