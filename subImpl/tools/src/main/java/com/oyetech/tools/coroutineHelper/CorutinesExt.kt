package com.oyetech.tools.coroutineHelper

import androidx.annotation.FloatRange
import com.oyetech.models.utils.const.HelperConstant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.pow

/**
Created by Erdi Özbek
-10.04.2022-
-17:51-
 **/

val exceptionHandler = CoroutineExceptionHandler { _, ex ->
    Timber.d("exceptionHandler == " + ex)
    ex.printStackTrace()
}

fun CoroutineScope.launchCustom(block: suspend CoroutineScope.() -> Unit): Job {
    return this.launch(exceptionHandler) {
        block()
    }
}

fun <T> Flow<T>.globalScopeOnEachTryCatch(
    action: ((T) -> Unit),
): Job {
    val flow = this
    return GlobalScope.launchCustom {
        try {
            flow.onEach {
                try {
                    action.invoke(it)
                } catch (e: Exception) {
                    triggerErrorEvent(e, true)
                }
            }.handleErrors().collect()
        } catch (e: Exception) {
            triggerErrorEvent(e, true)
        }
    }
}

fun triggerErrorEvent(e: Throwable, isPrintStack: Boolean = false) {
    if (isPrintStack) {
        e.printStackTrace()
    }
    Timber.d("errorr = " + e.message)
}

fun <T> Flow<T>.handleErrors(): Flow<T> = catch { e -> triggerErrorEvent(e, isPrintStack = true) }

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> {
    var job: Job = Job().apply { complete() }

    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                collect { value ->
                    if (!job.isActive) {
                        emit(value)
                        job = launch { delay(windowDuration) }
                    }
                }
            }
        }
    }
}

fun <T> Flow<T>.retryWhenWithExpDelay(
    @FloatRange(from = 0.0) initialDelay: Float = 1000f,
    @FloatRange(from = 1.0) retryFactor: Float = 2.0f,
    predicate: suspend FlowCollector<T>.(cause: Throwable, attempt: Long, delay: Long) -> Boolean,
): Flow<T> = this.retryWhen { cause, attempt ->
    val retryDelay = initialDelay * retryFactor.pow(attempt.toFloat())
    predicate(cause, attempt, retryDelay.toLong())
}

fun <T> Flow<T>.retryWhenWithDefault(
    @FloatRange(from = 0.0) initialDelay: Float = 1000f,
    @FloatRange(from = 1.0) retryFactor: Float = 2.0f,
    predicate: suspend FlowCollector<T>.(cause: Throwable, attempt: Long, delay: Long) -> Boolean,
): Flow<T> = this.retryWhen { cause, attempt ->
    val retryDelay = initialDelay * retryFactor.pow(attempt.toFloat())

    predicate(cause, attempt, retryDelay.toLong())
}

suspend fun <T> waitUntilNonNull(waitingObject: T?): Flow<T> {
    return flow<T> {
        // Simulating a condition where the value becomes non-null after a delay
        while (waitingObject != null) {
            delay(5) // Küçük bir bekleme süresi
        }
        emit(waitingObject as T)
    }
}

val periodicTimer = (0..Int.MAX_VALUE)
    .asSequence()
    .asFlow()
    .onEach { delay(HelperConstant.SENDING_TYPING_OPERATION_TIMEOUT) }

val periodicTimerDurationCalculator = (0..Int.MAX_VALUE)
    .asSequence()
    .asFlow()
    .onEach { delay(HelperConstant.EXOPLAYER_DURATION_CALCULATOR_TIME_TRIGGER) }

val periodicTimerEveryMin = (0..Int.MAX_VALUE)
    .asSequence()
    .asFlow()
    .onEach { delay(HelperConstant.SENDING_ONLINE_OPERATION) }

val periodicTimerForResendLocalMessage = (0..Int.MAX_VALUE)
    .asSequence()
    .asFlow()
    .onEach { delay(HelperConstant.SENDING_ONLINE_OPERATION) }

fun startTimerFlow(duration: Long): Flow<Long> = flow {
    var counter = 0L
    while (true) {
        emit(counter++)
        delay(duration) // Her 5 saniyede bir yay
    }
}
    .flowOn(Dispatchers.IO) // IO thread üzerinde çalıştır
    .catch { e ->
        // Hata durumlarını logla ve akışı devam ettir
        println("Error in Timer Flow: ${e.localizedMessage}")
        emit(-1L) // Hata durumunda fallback değeri yay
    }