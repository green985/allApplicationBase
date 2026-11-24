package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.stringKeys.StringKeys
import kotlin.time.Clock
import kotlin.time.Instant

internal const val MAX_CUSTOM_DURATION_MINUTES = 1440L

internal fun customDurationSeconds(minutes: String): Long? =
    minutes.takeIf { it.isNotEmpty() && it.all { char -> char in '0'..'9' } }
        ?.toLongOrNull()?.takeIf { it in 1..MAX_CUSTOM_DURATION_MINUTES }?.times(60)

internal fun presetFor(seconds: Long?): EntryTimerPreset? =
    if (seconds == null) {
        null
    } else {
        EntryTimerPreset.values()
            .firstOrNull { it.durationSeconds == seconds } ?: EntryTimerPreset.CUSTOM
    }

internal fun timerStatus(
    entry: EntryEntity,
    now: Instant = Clock.System.now(),
): EntryTimerStatus = when {
    entry.timerCancelledAt != null -> EntryTimerStatus.CANCELLED
    entry.completedAt != null -> EntryTimerStatus.FINISHED
    entry.timerEndsAt?.let { it <= now } == true -> EntryTimerStatus.FINISHED
    entry.timerStartedAt != null && entry.timerEndsAt != null -> EntryTimerStatus.RUNNING
    entry.durationSeconds != null -> EntryTimerStatus.PENDING
    else -> EntryTimerStatus.NONE
}

internal fun timerLabel(
    entry: EntryEntity,
    now: Instant = Clock.System.now(),
): String = when (timerStatus(entry, now)) {
    EntryTimerStatus.NONE -> ""
    EntryTimerStatus.PENDING -> StringKeys.pending
    EntryTimerStatus.RUNNING -> entry.timerEndsAt?.let { remainingLabel(it, now) }
        ?: StringKeys.pending

    EntryTimerStatus.FINISHED -> StringKeys.finished
    EntryTimerStatus.CANCELLED -> StringKeys.cancelled
}

private fun remainingLabel(end: Instant, now: Instant): String {
    val seconds = (end - now).inWholeSeconds.coerceAtLeast(0)
    return "${(seconds / 60).toString().padStart(2, '0')}:${
        (seconds % 60).toString().padStart(2, '0')
    }"
}
