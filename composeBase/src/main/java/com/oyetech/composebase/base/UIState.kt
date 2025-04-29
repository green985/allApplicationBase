package com.oyetech.composebase.base

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseUIEvent {

    fun classnameOrSomething() {

    }
}

abstract class BaseEvent {

    fun classnameOrSomething() {

    }
}

abstract class BaseUIState(
    open val isLoading: Boolean = false,
    open val errorMessage: String? = null,
)

inline fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    value = value.update()
}

private fun <T> MutableSharedFlow<T>.updateState(update: T.() -> T) {

}

suspend inline fun <T> MutableStateFlow<T>.updateListStateWithDelay(
    delay: Long,
    update: T.() -> T,
) {
    delay(delay)
    value = value.update()
}

