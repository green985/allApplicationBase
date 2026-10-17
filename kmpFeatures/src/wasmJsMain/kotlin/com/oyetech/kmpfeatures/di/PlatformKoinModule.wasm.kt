package com.oyetech.kmpfeatures.di

import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.dsl.module
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual val platformKoinModule = module {}

internal actual val diaryHtmlDownloader: ((String, String) -> Unit)? = ::downloadDiaryHtml

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun downloadDiaryHtml(fileName: String, html: String): Unit = js(
    """
    {
        const blob = new Blob([html], { type: "text/html;charset=utf-8" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        try {
            link.href = url;
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
        } finally {
            link.remove();
            setTimeout(() => URL.revokeObjectURL(url), 1000);
        }
    }
    """,
)

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
