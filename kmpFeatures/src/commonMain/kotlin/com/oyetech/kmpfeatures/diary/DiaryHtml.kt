package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.entity.EntryTimerStatus
import com.oyetech.kmpmodels.response.DiaryArchiveResponse
import com.oyetech.kmpmodels.response.DiaryQuoteResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Instant

internal fun diaryWeekStart(date: LocalDate): LocalDate =
    LocalDate.fromEpochDays(date.toEpochDays() - (date.dayOfWeek.ordinal + 1) % 7)

internal fun diaryWeekEnd(start: LocalDate): LocalDate =
    LocalDate.fromEpochDays(start.toEpochDays() + 6)

internal fun DiaryArchiveResponse.inDateRange(range: ClosedRange<LocalDate>?): DiaryArchiveResponse =
    if (range == null) this else copy(
        entries = entries.filter { LocalDate.parse(it.entryDate) in range },
        quotes = quotes.filter { LocalDate.parse(it.date) in range },
    )

internal fun diaryHtml(
    days: List<DiaryUiState>,
    title: String,
    archive: DiaryArchiveResponse? = null,
    now: Instant = Clock.System.now(),
): String = buildString {
    append("<!doctype html><html lang=\"tr\"><head><meta charset=\"UTF-8\">")
    append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
    append("<title>${title.escapeHtml()}</title>")
    append(
        """<style>
            body { font-family: system-ui, sans-serif; line-height: 1.55; max-width: 880px; margin: auto; padding: 24px; }
            blockquote { margin: 16px 0; padding: 12px; border-left: 3px solid; white-space: pre-wrap; overflow-wrap: anywhere; }
            .week { margin: 32px 0; border-top: 2px solid; }
            .day, .week-summary { margin: 20px 0; }
            .entry { border: 1px solid #aaa; border-radius: 8px; margin: 12px 0; padding: 12px 16px; }
            .entry p { margin: 6px 0; }
            .note { white-space: pre-wrap; overflow-wrap: anywhere; }
            .meta { font-size: 0.9em; overflow-wrap: anywhere; }
            summary { cursor: pointer; }
            dd { margin: 0 0 6px; overflow-wrap: anywhere; }
        </style></head><body>
        """.trimIndent(),
    )
    append("<h1>${title.escapeHtml()}</h1>")
    if (archive == null) {
        days.forEach { appendDiaryDay(it, "h2", now) }
    } else {
        val quotesByDate = archive.quotes.associateBy { it.date }
        append("<p>Süreler kayıtlara girilen değerlerdir; ölçülmüş çalışma süresi değildir.</p>")
        days.groupBy { diaryWeekStart(it.selectedDate) }.forEach { (start, week) ->
            append(
                "<section class=\"week\"><h2>${formatDate(start)} – ${
                    formatDate(
                        diaryWeekEnd(
                            start
                        )
                    )
                }</h2>"
            )
            week.forEach { appendDiaryDay(it, "h3", now, quotesByDate[it.selectedDate.toString()]) }
            appendWeekSummary(week.flatMap { it.entries }, now)
            append("</section>")
        }
    }
    if (archive != null) {
        val json = Json.encodeToString(archive).replace("<", "\\u003c")
        append("<script type=\"application/json\" id=\"diary-archive-data\">$json</script>")
    }
    append("</body></html>")
}

private fun StringBuilder.appendDiaryDay(
    snapshot: DiaryUiState,
    heading: String,
    now: Instant,
    quote: DiaryQuoteResponse? = null,
) {
    append("<section class=\"day\"><$heading>${snapshot.selectedDateLabel.escapeHtml()}</$heading>")
    if (snapshot.dayQuote.isNotBlank()) {
        append("<p><strong>${StringKeys.todaysQuote}</strong></p>")
        append("<blockquote>${snapshot.dayQuote.escapeHtml()}</blockquote>")
        if (quote != null) {
            append("<details class=\"meta\"><summary>Söz ayrıntıları</summary><dl>")
            quote.createdAt?.let {
                append(
                    "<dt>${StringKeys.entryCreatedAt}</dt><dd>${
                        exportTimestamp(
                            Instant.parse(it)
                        )
                    }</dd>"
                )
            }
            quote.updatedAt?.let {
                append(
                    "<dt>Güncelleme zamanı</dt><dd>${
                        exportTimestamp(
                            Instant.parse(
                                it
                            )
                        )
                    }</dd>"
                )
            }
            append("</dl></details>")
        }
    }
    if (snapshot.dailyThoughts.isNotBlank()) {
        append("<p><strong>${StringKeys.dailyThoughts}</strong></p>")
        append("<blockquote>${snapshot.dailyThoughts.escapeHtml()}</blockquote>")
    }
    if (snapshot.entries.isEmpty()) {
        append("<p>${StringKeys.noDiaryEntries}</p>")
    } else {
        snapshot.entries.sortedBy { it.createdAt }.forEach { entry ->
            append("<article class=\"entry\" id=\"entry-${entry.id.escapeHtml()}\">")
            append("<p><strong>${entryArea(entry).escapeHtml()}</strong> · ${entryTag(entry).escapeHtml()}</p>")
            append("<p class=\"meta\">${entryStatus(entry, now)}")
            entry.durationSeconds?.let { append(" · ${StringKeys.diaryExportDuration(it)}") }
            if (entry.isReminder) append(" · ${StringKeys.diaryReminders}")
            append("</p><p class=\"note\">${entry.text.escapeHtml()}</p>")
            append("<details><summary>Kayıt ayrıntıları</summary><dl>")
            listOf(
                "Kayıt kimliği" to entry.id,
                "Kayıt tarihi" to entry.entryDate,
                "Oluşturan" to entry.createdBy.takeIf { it.isNotBlank() },
                StringKeys.entryCreatedAt to entry.createdAt?.let(::exportTimestamp),
                "Güncelleme zamanı" to entry.updatedAt?.let(::exportTimestamp),
                StringKeys.timerStartedAt to entry.timerStartedAt?.let(::exportTimestamp),
                "Sayaç bitişi" to entry.timerEndsAt?.let(::exportTimestamp),
                "Sayaç iptali" to entry.timerCancelledAt?.let(::exportTimestamp),
                "Tamamlanma zamanı" to entry.completedAt?.let(::exportTimestamp),
                "Ek not" to entry.note?.takeIf { it != entry.text },
            ).forEach { (label, value) ->
                if (!value.isNullOrBlank()) append("<dt>${label.escapeHtml()}</dt><dd class=\"note\">${value.escapeHtml()}</dd>")
            }
            append("</dl></details></article>")
        }
    }
    append("</section>")
}

private fun StringBuilder.appendWeekSummary(entries: List<EntryEntity>, now: Instant) {
    append("<section class=\"week-summary\"><h3>Hafta özeti</h3>")
    append("<p>${entryTotals(entries, now)}</p>")
    val tags = entries.filter { it.tag != null }.groupBy { it.tag?.id }
    if (tags.isNotEmpty()) {
        append("<h4>Kullanılan etiketler</h4><ul>")
        tags.values.sortedBy { entryTag(it.first()).lowercase() }.forEach { group ->
            val entry = group.first()
            append(
                "<li>${entryTag(entry).escapeHtml()} (${entryArea(entry).escapeHtml()}) · ${
                    entryTotals(
                        group,
                        now
                    )
                }</li>"
            )
        }
        append("</ul>")
    }
    if (entries.isNotEmpty()) append("<h4>Alanlara göre kayıtlar</h4>")
    entries.groupBy { it.areaId }.values
        .sortedBy { entryArea(it.first()).lowercase() }
        .forEach { area ->
            append(
                "<details open><summary><strong>${entryArea(area.first()).escapeHtml()}</strong> · ${
                    entryTotals(
                        area,
                        now
                    )
                }</summary>"
            )
            area.groupBy { it.tag?.id }.values
                .sortedBy { entryTag(it.first()).lowercase() }
                .forEach { group ->
                    append("<h5>${entryTag(group.first()).escapeHtml()} · ${group.size} kayıt</h5><ul>")
                    group.forEach { entry ->
                        val date = entry.entryDate?.escapeHtml().orEmpty()
                        append(
                            "<li><a href=\"#entry-${entry.id.escapeHtml()}\">$date</a> · ${
                                entryStatus(
                                    entry,
                                    now
                                )
                            }"
                        )
                        entry.durationSeconds?.let { append(" · ${StringKeys.diaryExportDuration(it)}") }
                        if (entry.text.isNotBlank()) append("<div class=\"note\">${entry.text.escapeHtml()}</div>")
                        append("</li>")
                    }
                    append("</ul>")
                }
            append("</details>")
        }
    append("</section>")
}

private fun entryArea(entry: EntryEntity): String =
    entry.areaId?.let(::areaLabel) ?: "Alan belirtilmemiş"

private fun entryTag(entry: EntryEntity): String = entry.tag?.name ?: "Etiketsiz"

private fun isEntryFinished(entry: EntryEntity, now: Instant): Boolean =
    entry.completedAt != null || timerStatus(entry, now) == EntryTimerStatus.FINISHED

private fun entryStatus(entry: EntryEntity, now: Instant): String = when {
    isEntryFinished(entry, now) -> StringKeys.alreadyDone
    else -> when (timerStatus(entry, now)) {
        EntryTimerStatus.RUNNING -> "Devam ediyor"
        EntryTimerStatus.PENDING -> StringKeys.pending
        EntryTimerStatus.CANCELLED -> StringKeys.cancelled
        else -> "Tamamlanmadı"
    }
}

private fun entryTotals(entries: List<EntryEntity>, now: Instant): String {
    val (finished, unfinished) = entries.partition { isEntryFinished(it, now) }
    val done = StringKeys.diaryExportDuration(finished.sumOf { it.durationSeconds ?: 0 })
    val remaining = StringKeys.diaryExportDuration(unfinished.sumOf { it.durationSeconds ?: 0 })
    return "${entries.size} kayıt · $done tamamlandı · $remaining tamamlanmadı"
}

private fun exportTimestamp(instant: Instant): String {
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.date} ${formatTime(instant)}"
}

private fun String.escapeHtml(): String = replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")
