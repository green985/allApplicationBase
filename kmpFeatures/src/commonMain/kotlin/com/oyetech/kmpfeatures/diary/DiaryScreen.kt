package com.oyetech.kmpfeatures.diary

import allapplicationbase.kmpfeatures.generated.resources.Res
import allapplicationbase.kmpfeatures.generated.resources.diary_area_emoji
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.OperationState
import com.oyetech.viewmodule.AppColors
import com.oyetech.viewmodule.ViewModuleTheme
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun DiaryScreenSetup() {
    val operatorScope = rememberCoroutineScope()
    val operator = koinInject<DiaryOperator>(
        parameters = { parametersOf(operatorScope) },
    )
    val uiState by operator.state.collectAsState()

    DiaryScreen(
        uiState = uiState,
        onAction = operator::dispatch,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DiaryScreen(
    uiState: DiaryUiState,
    onAction: (DiaryAction) -> Unit,
) {
    val areaEmojiFont = FontFamily(Font(Res.font.diary_area_emoji))
    val areaMenuSpacing = with(LocalDensity.current) { 8.dp.roundToPx() }
    var areaMenuOffset by remember { mutableStateOf(IntOffset.Zero) }

    Scaffold { contentPadding ->
        if (uiState.isReminderListVisible) {
            DiaryReminderList(uiState, onAction, Modifier.padding(contentPadding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (uiState.isBulkEntryAvailable) {
                Button(onClick = { onAction(DiaryAction.RemindersOpened) }) {
                    BadgedBox(badge = { uiState.reminderCountLabel?.let { Badge { Text(it) } } }) {
                        Text(StringKeys.diaryReminders)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    enabled = uiState.canGoPrevious,
                    onClick = { onAction(DiaryAction.PreviousDayClicked) },
                ) {
                    Text(StringKeys.yesterday)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = uiState.selectedDateLabel,
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.textPrimary,
                        )
                        Text(
                            text = if (uiState.isTodaySelected) StringKeys.today else StringKeys.selectedDay,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.textSecondary,
                        )
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above,
                        ),
                        tooltip = { PlainTooltip { Text(StringKeys.returnToToday) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            enabled = uiState.canGoPrevious,
                            onClick = { onAction(DiaryAction.TodayClicked) },
                        ) {
                            val iconColor = AppColors.textPrimary
                            Icon(
                                imageVector = remember(iconColor) {
                                    ImageVector.Builder(
                                        name = "Home",
                                        defaultWidth = 24.dp,
                                        defaultHeight = 24.dp,
                                        viewportWidth = 24f,
                                        viewportHeight = 24f,
                                    ).apply {
                                        path(fill = SolidColor(iconColor)) {
                                            moveTo(12f, 3f)
                                            lineTo(2f, 12f)
                                            lineTo(5f, 12f)
                                            lineTo(5f, 21f)
                                            lineTo(10f, 21f)
                                            lineTo(10f, 15f)
                                            lineTo(14f, 15f)
                                            lineTo(14f, 21f)
                                            lineTo(19f, 21f)
                                            lineTo(19f, 12f)
                                            lineTo(22f, 12f)
                                            close()
                                        }
                                    }.build()
                                },
                                contentDescription = StringKeys.returnToToday,
                            )
                        }
                    }
                }
                Button(
                    enabled = uiState.canGoNext,
                    onClick = { onAction(DiaryAction.NextDayClicked) },
                ) {
                    Text(StringKeys.tomorrow)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Box {
                        Button(
                            modifier = Modifier.onSizeChanged {
                                areaMenuOffset = IntOffset(it.width + areaMenuSpacing, 0)
                            },
                            onClick = { onAction(DiaryAction.AreaMenuClicked) },
                        ) {
                            Text(StringKeys.areas)
                        }
                        if (uiState.isAreaMenuVisible) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = areaMenuOffset,
                                onDismissRequest = { onAction(DiaryAction.AreaMenuDismissed) },
                                properties = PopupProperties(focusable = true),
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    shadowElevation = 6.dp,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        uiState.areaOptions.forEach { area ->
                                            TextButton(
                                                enabled = uiState.canGoPrevious,
                                                onClick = {
                                                    onAction(DiaryAction.AreaFilterSelected(area.area))
                                                },
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    Text(
                                                        text = area.emoji,
                                                        fontFamily = areaEmojiFont,
                                                        style = MaterialTheme.typography.titleLarge,
                                                    )
                                                    Text(area.label, maxLines = 1, softWrap = false)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { onAction(DiaryAction.AddEntryClicked) },
                ) {
                    Text(StringKeys.addEntry)
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.isHtmlExportAvailable) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (uiState.isDailyContentVisible) {
                                Button(onClick = { onAction(DiaryAction.ExportDiaryHtmlClicked) }) {
                                    Text(
                                        text = StringKeys.downloadDiaryHtml,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                }
                            }
                            Button(
                                enabled = uiState.canExportArchive,
                                onClick = { onAction(DiaryAction.ExportDiaryArchiveClicked) },
                            ) {
                                Text(
                                    text = StringKeys.downloadDiaryArchive,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                            Button(
                                enabled = uiState.canExportArchive,
                                onClick = { onAction(DiaryAction.ExportDiaryWeeklyClicked) },
                            ) {
                                Text(StringKeys.downloadDiaryWeekly, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
            if (uiState.isBulkEntryAvailable) {
                Button(onClick = { onAction(DiaryAction.BulkEntryClicked) }) {
                    Text(StringKeys.bulkEntryTitle)
                }
            }
            if (uiState.isDailyContentVisible) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.dayQuote,
                    onValueChange = { onAction(DiaryAction.QuoteChanged(it)) },
                    label = { Text(StringKeys.todaysQuote) },
                    placeholder = { Text(StringKeys.writeTodaysQuote) },
                    singleLine = true,
                )
            } else {
                Text(
                    text = uiState.areaFilterLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.textPrimary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FilterChip(
                        selected = uiState.isAllDatesSelected,
                        onClick = { onAction(DiaryAction.ListAllDatesClicked) },
                        label = { Text(StringKeys.allDates) },
                    )
                    FilterChip(
                        selected = uiState.isWeeklyDateFilterSelected,
                        onClick = { onAction(DiaryAction.ListWeeklyClicked) },
                        label = { Text(StringKeys.lastSevenDays) },
                    )
                    FilterChip(
                        selected = uiState.isMonthlyDateFilterSelected,
                        onClick = { onAction(DiaryAction.ListMonthlyClicked) },
                        label = { Text(StringKeys.lastThirtyDays) },
                    )
                    FilterChip(
                        selected = uiState.isCustomDateFilterSelected,
                        onClick = { onAction(DiaryAction.ListCustomDateRangeClicked) },
                        label = { Text(StringKeys.customDateRange) },
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FilterChip(
                        selected = uiState.isAllListTagsSelected,
                        onClick = { onAction(DiaryAction.ListTagFilterSelected(null)) },
                        label = { Text(StringKeys.allTags) },
                    )
                    uiState.listTagOptions.forEach { tag ->
                        FilterChip(
                            selected = tag.isSelected,
                            onClick = { onAction(DiaryAction.ListTagFilterSelected(tag.id)) },
                            label = { Text(tag.displayLabel) },
                        )
                    }
                }
                when (val operation = uiState.listTagsOperation) {
                    OperationState.Idle -> Unit
                    OperationState.Loading -> Unit
                    is OperationState.Error -> {
                        Text(operation.error.message, color = AppColors.textSecondary)
                        TextButton(
                            onClick = { onAction(DiaryAction.RetryListTagsLoadClicked) },
                        ) {
                            Text(StringKeys.retry)
                        }
                    }
                }
            }
            when (val operation = uiState.entriesOperation) {
                OperationState.Idle -> Unit
                OperationState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is OperationState.Error -> {
                    Text(
                        text = operation.error.message,
                        color = AppColors.textSecondary,
                    )
                    Button(
                        onClick = { onAction(DiaryAction.RetryEntriesLoadClicked) },
                    ) {
                        Text(StringKeys.retry)
                    }
                }
            }
            if (uiState.isAreaEntriesEmpty) {
                Text(uiState.areaEntriesEmptyMessage, color = AppColors.textSecondary)
            }
            uiState.entryItems.forEach { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { onAction(DiaryAction.EntryEditClicked(entry.id)) },
                    onDelete = { onAction(DiaryAction.EntryDeleteClicked(entry.id)) },
                    onStart = { onAction(DiaryAction.StartEntryTimerClicked(entry.id)) },
                    onCancel = { onAction(DiaryAction.CancelEntryTimerClicked(entry.id)) },
                    onComplete = { onAction(DiaryAction.EntryCompletedClicked(entry.id)) },
                    onAddReminder = if (uiState.isBulkEntryAvailable) {
                        { onAction(DiaryAction.AddReminderClicked(entry.id)) }
                    } else null,
                    isAddReminderEnabled = !uiState.isReminderChanging && !entry.isTimerRunning,
                )
            }

            Button(onClick = { onAction(DiaryAction.BackClicked) }) {
                Text(StringKeys.back)
            }
        }
    }
    if (uiState.isDateRangeDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(DiaryAction.ListDateRangeDismissed) },
            title = {
                Text(if (uiState.isWeeklyExportDateRange) StringKeys.diaryWeeklyReport else StringKeys.customDateRange)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.dateRangeStartInput,
                        onValueChange = { onAction(DiaryAction.ListDateRangeStartChanged(it)) },
                        label = { Text(StringKeys.rangeStartDate) },
                        placeholder = { Text(StringKeys.dateInputFormat) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.dateRangeEndInput,
                        onValueChange = { onAction(DiaryAction.ListDateRangeEndChanged(it)) },
                        label = { Text(StringKeys.rangeEndDate) },
                        placeholder = { Text(StringKeys.dateInputFormat) },
                        singleLine = true,
                    )
                    uiState.dateRangeErrorMessage?.let { message ->
                        Text(message, color = AppColors.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onAction(DiaryAction.ApplyListDateRangeClicked) }) {
                    Text(if (uiState.isWeeklyExportDateRange) StringKeys.diaryWeeklyDownloadConfirm else StringKeys.applyDateRange)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(DiaryAction.ListDateRangeDismissed) }) {
                    Text(StringKeys.cancelDateRange)
                }
            },
        )
    }
    if (uiState.isEditorVisible) {
        Dialog(
            onDismissRequest = { onAction(DiaryAction.EntryDialogDismissed) },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            EntryEditor(
                uiState = uiState,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun DiaryReminderList(
    uiState: DiaryUiState,
    onAction: (DiaryAction) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(
            enabled = !uiState.isReminderChanging,
            onClick = { onAction(DiaryAction.RemindersClosed) },
        ) { Text(StringKeys.back) }
        Text(StringKeys.diaryReminders, style = MaterialTheme.typography.headlineSmall)
        when (val operation = uiState.reminderListOperation) {
            OperationState.Idle -> Unit
            OperationState.Loading -> CircularProgressIndicator()
            is OperationState.Error -> {
                Text(operation.error.message)
                TextButton(onClick = { onAction(DiaryAction.RemindersOpened) }) { Text(StringKeys.retry) }
            }
        }
        if (uiState.isReminderListEmpty) Text(StringKeys.diaryRemindersEmpty)
        uiState.reminderItems.forEach { entry ->
            DiaryEntryCard(
                entry = entry,
                onClick = { onAction(DiaryAction.EntryEditClicked(entry.id)) },
                onDelete = { onAction(DiaryAction.EntryDeleteClicked(entry.id)) },
                onStart = { onAction(DiaryAction.StartEntryTimerClicked(entry.id)) },
                onCancel = { onAction(DiaryAction.CancelEntryTimerClicked(entry.id)) },
                onComplete = { onAction(DiaryAction.EntryCompletedClicked(entry.id)) },
                onCopyReminder = { onAction(DiaryAction.CopyReminderToTodayClicked(entry.id)) },
                actionsEnabled = !uiState.isReminderChanging && uiState.entryOperation !is OperationState.Loading,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiaryScreenPreview() {
    val entries = EntryDummyData().generate()
    ViewModuleTheme {
        DiaryScreen(
            uiState = DiaryOperator.initialState().copy(
                entries = entries,
                entryItems = renderEntryItems(entries),
                isEditorVisible = false,
                isHtmlExportAvailable = true
            ),
            onAction = {},
        )
    }
}
