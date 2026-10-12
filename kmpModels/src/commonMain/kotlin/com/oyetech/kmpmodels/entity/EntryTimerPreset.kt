package com.oyetech.kmpmodels.entity

enum class EntryTimerPreset(val durationSeconds: Long?) {
    ALREADY_DONE(null),
    FIVE_MINUTES(5 * 60L),
    TEN_MINUTES(10 * 60L),
    FIFTEEN_MINUTES(15 * 60L),
    TWENTY_MINUTES(20 * 60L),
    CUSTOM(null),
}

enum class EntryTimerStatus {
    PENDING,
    RUNNING,
    FINISHED,
    CANCELLED,
}
