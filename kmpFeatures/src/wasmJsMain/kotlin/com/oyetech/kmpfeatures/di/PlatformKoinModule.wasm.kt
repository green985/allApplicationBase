package com.oyetech.kmpfeatures.di

import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.dsl.module
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual val platformKoinModule = module {}

internal actual suspend fun playTimerFinishedSound() {
    suspendCancellableCoroutine { continuation ->
        playTimerSound { error ->
            if (continuation.isActive) {
                if (error.isEmpty()) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(IllegalStateException(error))
                }
            }
        }
    }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun playTimerSound(onComplete: (String) -> Unit): Unit = js(
    """
    {
        window.KMP_PLAY_TIMER_FINISHED_SOUND()
            .then(() => onComplete(""))
            .catch((error) => onComplete(String(error)));
    }
    """,
)
