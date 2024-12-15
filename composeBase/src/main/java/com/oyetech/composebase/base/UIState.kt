package com.oyetech.composebase.base

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

//
//sealed class UIState<out T> {
//    object Loading : UIState<Nothing>()
//    object Empty : UIState<Nothing>()
//    data class Success<T>(val data: T) : UIState<T>()
//    data class Error(val message: String) : UIState<Nothing>()
//}

sealed class UIState {
}

inline fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    value = value.update()
}

suspend inline fun <T> MutableStateFlow<T>.updateListStateWithDelay(
    delay: Long,
    update: T.() -> T,
) {
    delay(delay)
    value = value.update()
}

