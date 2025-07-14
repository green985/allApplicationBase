package com.oyetech.composebase.helpers.eventNavigator

import androidx.lifecycle.LifecycleCoroutineScope
import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorEvent
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent
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
        return
        lifecycleScope.launch(Dispatchers.IO) {
            triggerEvents(getDummyEventList())
        }

    }

    companion object {
        fun getDummyEventList(): List<BaseEvent> {
            return buildList {
                add(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
                add(MessageConversationListEvent.OnConversationClickWithPosition(1))
            }
        }

        fun getDummyEvent(): BaseEvent {
            return object : BaseEvent() {}
        }
    }
}