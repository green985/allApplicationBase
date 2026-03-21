package com.oyetech.composebase.sharedViews.floating

import com.oyetech.composebase.base.BaseEvent

/**
 * Events for FloatingAskQuestionBar
 */
sealed class FloatingAskQuestionBarEvent : BaseEvent() {
    object OnFabClicked : FloatingAskQuestionBarEvent()
}
