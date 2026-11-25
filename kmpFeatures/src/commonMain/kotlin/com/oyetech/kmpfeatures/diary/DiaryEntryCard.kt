package com.oyetech.kmpfeatures.diary

import allapplicationbase.kmpfeatures.generated.resources.Res
import allapplicationbase.kmpfeatures.generated.resources.diary_area_emoji
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.viewmodule.AppColors
import org.jetbrains.compose.resources.Font

@Composable
internal fun DiaryEntryCard(
    entry: DiaryEntryUiState,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onAddReminder: (() -> Unit)? = null,
    isAddReminderEnabled: Boolean = true,
) {
    val areaEmojiFont = FontFamily(Font(Res.font.diary_area_emoji))

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (entry.isAreaVisible) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = entry.areaEmoji,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = areaEmojiFont,
                            )
                            Text(
                                text = entry.areaLabel,
                                style = MaterialTheme.typography.titleLarge,
                                color = AppColors.textSecondary,
                            )
                        }
                    }
                    entry.tagLabel?.let { label ->
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.textPrimary,
                    )
                }
                Column {
                    Button(onClick = onDelete) { Text(StringKeys.delete) }
                    if (entry.isTimerStatusVisible) {
                        Text(entry.timerStatusLabel, color = AppColors.textSecondary)
                    }
                    if (entry.canStartTimer) Button(onClick = onStart) { Text(StringKeys.start) }
                    if (entry.canCancelTimer) Button(onClick = onCancel) { Text(StringKeys.cancelTimer) }
                    if (entry.canMarkCompleted) Button(onClick = onComplete) { Text(StringKeys.alreadyDone) }
                }
            }
            entry.dateLabel?.let { dateLabel ->
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textSecondary,
                )
            }
            Text(
                text = entry.timeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary,
            )
            if (onAddReminder != null) {
                TextButton(onClick = onAddReminder, enabled = isAddReminderEnabled) {
                    Text(StringKeys.diaryAddReminder)
                }
            }
        }
    }
}
