package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpmodels.response.DiaryArchiveResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Instant

internal fun diaryHtml(
    days: List<DiaryUiState>,
    title: String,
    archive: DiaryArchiveResponse? = null,
): String = buildString {
    append("<!doctype html><html lang=\"tr\"><head><meta charset=\"UTF-8\">")
    append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
    append("<title>${title.escapeHtml()}</title>")
    append(
        """<style>
            body { font-family: sans-serif; max-width: 1100px; margin: auto; padding: 24px; }
            blockquote { margin: 16px 0; padding: 12px; border-left: 3px solid; white-space: pre-wrap; overflow-wrap: anywhere; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ccc; padding: 10px; text-align: left; vertical-align: top; }
            td { white-space: pre-wrap; overflow-wrap: anywhere; }
            .entries { overflow-x: auto; }
            section { margin-bottom: 40px; }
        </style></head><body>
        """.trimIndent(),
    )
    append("<h1>${title.escapeHtml()}</h1>")
    days.forEach { day ->
        append("<section><h2>${day.selectedDateLabel.escapeHtml()}</h2>")
        appendDiaryDay(day)
        append("</section>")
    }
    if (archive != null) {
        val json = Json.encodeToString(archive).replace("<", "\\u003c")
        append("<script type=\"application/json\" id=\"diary-archive-data\">$json</script>")
    }
    append("</body></html>")
}

private fun StringBuilder.appendDiaryDay(snapshot: DiaryUiState) {
    append("<h3>${StringKeys.todaysQuote}</h3>")
    append("<blockquote>${snapshot.dayQuote.escapeHtml()}</blockquote>")
    if (snapshot.entries.isEmpty()) {
        append("<p>${StringKeys.noDiaryEntries}</p>")
    } else {
        append("<div class=\"entries\"><table><thead><tr>")
        listOf(
            StringKeys.entryCreatedAt,
            StringKeys.timerStartedAt,
            StringKeys.area,
            StringKeys.tag,
            StringKeys.note,
            StringKeys.duration,
        ).forEach { append("<th scope=\"col\">${it.escapeHtml()}</th>") }
        append("</tr></thead><tbody>")
        snapshot.entries.sortedBy { it.createdAt }.forEach { entry ->
            append("<tr>")
            listOf(
                entry.createdAt?.let(::exportTimestamp) ?: "-",
                entry.timerStartedAt?.let(::exportTimestamp) ?: "-",
                areaLabel(entry.areaId),
                entry.tag?.name.orEmpty(),
                entry.text,
                entry.durationSeconds?.let(StringKeys::diaryExportDuration) ?: "-",
            ).forEach { append("<td>${it.escapeHtml()}</td>") }
            append("</tr>")
        }
        append("</tbody></table></div>")
    }
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
