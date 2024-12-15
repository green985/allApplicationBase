package com.oyetech.core.coroutineHelper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
Created by Erdi Özbek
-10.12.2024-
-15:31-
 **/

fun <T> Flow<T>.asResult(): Flow<Result<T>> = flow {
    try {
        collect { value ->
            emit(Result.success(value)) // Emit success result
        }
    } catch (e: Throwable) {
        emit(Result.failure(e)) // Emit failure result
    }
}

fun <T> Flow<T>.asResultWithInitial(initial: T): Flow<Result<T>> = flow {
    emit(Result.success(initial)) // Emit initial value
    try {
        collect { value ->
            emit(Result.success(value))
        }
    } catch (e: Throwable) {
        emit(Result.failure(e))
    }
}