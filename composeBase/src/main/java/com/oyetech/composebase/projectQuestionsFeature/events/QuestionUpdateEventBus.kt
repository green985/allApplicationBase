package com.oyetech.composebase.projectQuestionsFeature.events

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class QuestionUpdateEventBus {
    private val _questionUpdatedEvent = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val questionUpdatedEvent: SharedFlow<String> = _questionUpdatedEvent.asSharedFlow()

    suspend fun emitQuestionUpdated(questionId: String) {
        _questionUpdatedEvent.emit(questionId)
    }
}
