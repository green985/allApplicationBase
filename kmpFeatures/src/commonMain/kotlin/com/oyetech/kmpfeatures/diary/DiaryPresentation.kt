package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerPreset
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.entity.TagEntry
import com.oyetech.kmpmodels.response.EntryResponse
import com.oyetech.kmpmodels.response.TagResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryTimerPresetUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

private val diaryAreas = listOf(
    AreaEntry.WORK,
    AreaEntry.BODY,
    AreaEntry.MIND,
    AreaEntry.CHARACTER,
    AreaEntry.LIFE,
)

internal fun renderEntryItems(
    entries: List<EntryEntity>,
    now: Instant = Clock.System.now(),
    selectedListArea: AreaEntry? = null,
    selectedListTagId: String? = null,
    listStartDate: LocalDate? = null,
    listEndDate: LocalDate? = null,
): List<DiaryEntryUiState> {
    val areas = areaOptions()
    val visibleEntries = if (selectedListArea == null) {
        entries.sortedByDescending { it.createdAt }
    } else {
        entries.filter {
            it.areaId == selectedListArea.name &&
                    (selectedListTagId == null || it.tag?.id == selectedListTagId) &&
                    (
                            (listStartDate == null && listEndDate == null) ||
                                    it.entryDate?.let { date ->
                                        val entryDate = LocalDate.parse(date)
                                        (listStartDate == null || entryDate >= listStartDate) &&
                                                (listEndDate == null || entryDate <= listEndDate)
                                    } == true
                            )
        }
            .sortedWith(
                compareByDescending<EntryEntity> { it.entryDate }
                    .thenByDescending { it.createdAt }
                    .thenByDescending { it.id },
            )
    }
    return visibleEntries.map { entry ->
        val status = timerStatus(entry, now)
        val label = timerLabel(entry, now)
        DiaryEntryUiState(
            id = entry.id,
            areaLabel = areaLabel(entry.areaId),
            createdBy = entry.createdBy,
            timeLabel = entry.createdAt?.let(::formatTime).orEmpty(),
            dateLabel = if (selectedListArea == null) null else entry.entryDate?.let {
                formatDate(LocalDate.parse(it))
            },
            text = entry.text,
            timerStatusLabel = label,
            countdownLabel = if (status == EntryTimerStatus.RUNNING) label else null,
            isTimerRunning = status == EntryTimerStatus.RUNNING,
            canStartTimer = status == EntryTimerStatus.PENDING && entry.durationSeconds != null,
            canCancelTimer = status == EntryTimerStatus.RUNNING,
            canMarkCompleted = status == EntryTimerStatus.NONE ||
                    status == EntryTimerStatus.PENDING || status == EntryTimerStatus.RUNNING,
            isAreaVisible = entry.areaId != null,
            tagLabel = entry.tag?.let { "#${it.name}" },
            areaEmoji = areas.firstOrNull { it.area.name == entry.areaId }?.emoji.orEmpty(),
            isTimerStatusVisible = status != EntryTimerStatus.NONE,
        )
    }
}

internal fun entryEntity(response: EntryResponse): EntryEntity =
    EntryEntity(
        id = response.id,
        areaId = response.areaId,
        tag = response.tag?.let(::tagEntity),
        text = response.text,
        entryDate = response.entryDate,
        durationSeconds = response.durationSeconds,
        timerStartedAt = response.timerStartedAt?.let(Instant::parse),
        timerEndsAt = response.timerEndsAt?.let(Instant::parse),
        timerCancelledAt = response.timerCancelledAt?.let(Instant::parse),
        completedAt = response.completedAt?.let(Instant::parse),
        createdBy = response.createdBy.orEmpty(),
        note = response.note,
        createdAt = response.createdAt?.let(Instant::parse),
        updatedAt = response.updatedAt?.let(Instant::parse),
    )

internal fun tagEntity(response: TagResponse): TagEntry =
    TagEntry(response.id, response.name, response.areaId)

internal fun normalizeTagName(value: String): String =
    value.trim().replace(Regex("\\s+"), " ")

internal fun areaOptions(): List<DiaryAreaUiState> =
    diaryAreas.map { area ->
        val (emoji, description) = when (area) {
            AreaEntry.WORK -> "🏗️" to StringKeys.workAreaDescription
            AreaEntry.BODY -> "💪" to StringKeys.bodyAreaDescription
            AreaEntry.MIND -> "🧠" to StringKeys.mindAreaDescription
            AreaEntry.CHARACTER -> "🧭" to StringKeys.characterAreaDescription
            AreaEntry.LIFE -> "🌍" to StringKeys.lifeAreaDescription
        }
        DiaryAreaUiState(
            area = area,
            label = areaLabel(area.name),
            emoji = emoji,
            description = description,
        )
    }

internal fun timerPresetOptions(): List<DiaryTimerPresetUiState> =
    EntryTimerPreset.values().filter { it != EntryTimerPreset.ALREADY_DONE }.map { preset ->
        DiaryTimerPresetUiState(
            preset = preset,
            label = when (preset) {
                EntryTimerPreset.ALREADY_DONE -> StringKeys.alreadyDone
                EntryTimerPreset.FIVE_MINUTES -> StringKeys.fiveMinutes
                EntryTimerPreset.TEN_MINUTES -> StringKeys.tenMinutes
                EntryTimerPreset.FIFTEEN_MINUTES -> StringKeys.fifteenMinutes
                EntryTimerPreset.TWENTY_MINUTES -> StringKeys.twentyMinutes
                EntryTimerPreset.CUSTOM -> StringKeys.custom
            },
        )
    }

internal fun formatDate(date: LocalDate): String =
    "${date.day} ${StringKeys.turkishMonths[date.monthNumber - 1]} ${date.year}"

internal fun formatTime(instant: kotlin.time.Instant): String {
    val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${localTime.hour.toString().padStart(2, '0')}:${
        localTime.minute.toString().padStart(2, '0')
    }"
}

internal fun areaLabel(areaId: String?): String = when (areaId) {
    null -> ""
    AreaEntry.WORK.name -> StringKeys.workArea
    AreaEntry.BODY.name -> StringKeys.bodyArea
    AreaEntry.MIND.name -> StringKeys.mindArea
    AreaEntry.CHARACTER.name -> StringKeys.characterArea
    AreaEntry.LIFE.name -> StringKeys.lifeArea
    else -> areaId
}
