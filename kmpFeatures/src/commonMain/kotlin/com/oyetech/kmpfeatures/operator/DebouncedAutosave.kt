package com.oyetech.kmpfeatures.operator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.scheduleDebouncedSave(
    previousJob: Job?,
    delayMillis: Long = 700L,
    canSave: () -> Boolean,
    onSave: () -> Unit,
): Job {
    previousJob?.cancel()
    return launch {
        delay(delayMillis)
        if (canSave()) onSave()
    }
}

fun Job?.cancelPendingSave() {
    this?.cancel()
}
