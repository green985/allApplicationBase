package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.TimerTypeEntry
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class EntryDummyData {
    fun generate(now: Instant = Clock.System.now()): List<EntryEntity> {
        val entry = EntryEntity(
            id = "entry-running",
            areaId = AreaEntry.WORK.name,
            text = "Proje planlama",
            entryDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
            durationSeconds = 600,
            timerStartedAt = now - 2.minutes,
            timerEndsAt = now + 8.minutes,
            createdBy = "Demo",
            createdAt = now - 2.minutes,
            updatedAt = now - 2.minutes,
            timerType = TimerTypeEntry.COUNTDOWN,
        )
        return listOf(
            entry,
            entry.copy(
                id = "entry-pending",
                areaId = AreaEntry.MIND.name,
                text = "Kitap okuma",
                durationSeconds = 900,
                timerStartedAt = null,
                timerEndsAt = null,
                createdAt = now - 15.minutes,
                updatedAt = now - 15.minutes,
            ),
            entry.copy(
                id = "entry-finished",
                areaId = AreaEntry.BODY.name,
                text = "Egzersiz",
                durationSeconds = 300,
                timerStartedAt = now - 30.minutes,
                timerEndsAt = now - 25.minutes,
                completedAt = now - 25.minutes,
                createdAt = now - 30.minutes,
                updatedAt = now - 25.minutes,
            ),
            entry.copy(
                id = "entry-cancelled",
                areaId = AreaEntry.HEALTH.name,
                text = "Meditasyon",
                timerStartedAt = now - 45.minutes,
                timerEndsAt = now - 35.minutes,
                timerCancelledAt = now - 42.minutes,
                createdAt = now - 45.minutes,
                updatedAt = now - 42.minutes,
            ),
            entry.copy(
                id = "entry-without-timer",
                areaId = AreaEntry.LIFE.name,
                text = "Gunluk not",
                durationSeconds = null,
                timerStartedAt = null,
                timerEndsAt = null,
                completedAt = now - 60.minutes,
                createdAt = now - 60.minutes,
                updatedAt = now - 60.minutes,
                timerType = null,
            ),
        )
    }
}
