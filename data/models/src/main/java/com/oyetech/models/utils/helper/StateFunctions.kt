package com.oyetech.models.utils.helper

import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-24.12.2024-
-23:16-
 **/

inline fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    value = value.update()
}