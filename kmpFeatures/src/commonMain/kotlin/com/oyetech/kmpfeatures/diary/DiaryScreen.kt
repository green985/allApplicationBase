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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                        }
                    }
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
            }
            when (val operation = uiState.entriesOperation) {
                OperationState.Idle -> Unit
                OperationState.Loading -> CircularProgressIndicator()
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
                Text(StringKeys.noAreaEntries, color = AppColors.textSecondary)
            }
            uiState.entryItems.forEach { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = { onAction(DiaryAction.EntryEditClicked(entry.id)) },
                    onDelete = { onAction(DiaryAction.EntryDeleteClicked(entry.id)) },
                    onStart = { onAction(DiaryAction.StartEntryTimerClicked(entry.id)) },
                    onCancel = { onAction(DiaryAction.CancelEntryTimerClicked(entry.id)) },
                    onComplete = { onAction(DiaryAction.EntryCompletedClicked(entry.id)) },
                )
            }
            Button(onClick = { onAction(DiaryAction.BackClicked) }) {
                Text(StringKeys.back)
            }
        }
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
private fun DiaryEntryCard(
    entry: DiaryEntryUiState,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
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
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EntryEditor(
    uiState: DiaryUiState,
    onAction: (DiaryAction) -> Unit,
) {
    val noteFocusRequester = remember { FocusRequester() }
    val areaEmojiFont = FontFamily(Font(Res.font.diary_area_emoji))

    LaunchedEffect(Unit) {
        noteFocusRequester.requestFocus()
    }

    OutlinedCard(
        modifier = Modifier
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Escape) {
                    if (event.type == KeyEventType.KeyDown) {
                        onAction(DiaryAction.EntryDialogDismissed)
                    }
                    true
                } else {
                    false
                }
            }
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = StringKeys.newEntry,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.textPrimary,
                )
                IconButton(
                    onClick = { onAction(DiaryAction.EntryDialogCancelled) },
                ) {
                    Text(
                        modifier = Modifier.semantics {
                            contentDescription = StringKeys.cancelEntry
                        },
                        text = "\u00D7",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
            Text(
                text = StringKeys.optionalArea,
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary,
            )
            uiState.areaOptionRows.forEach { rowAreas ->
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rowAreas.forEachIndexed { index, areaOption ->
                        Box(modifier = Modifier.weight(1f)) {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = areaOption.description,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                },
                                state = rememberTooltipState(),
                            ) {
                                this@SingleChoiceSegmentedButtonRow.SegmentedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    selected = uiState.selectedArea == areaOption.area,
                                    onClick = { onAction(DiaryAction.AreaSelected(areaOption.area)) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = rowAreas.size,
                                    ),
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = areaOption.emoji,
                                            fontSize = 16.sp,
                                            fontFamily = areaEmojiFont,
                                        )
                                        Text(
                                            text = areaOption.label,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (uiState.isTagInputVisible) {
                Text(
                    text = StringKeys.optionalTag,
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.textSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = 280.dp),
                        value = uiState.tagQuery,
                        onValueChange = { onAction(DiaryAction.TagQueryChanged(it)) },
                        label = { Text(StringKeys.tagSearchPlaceholder) },
                        singleLine = true,
                        isError = uiState.hasTagQueryError,
                        supportingText = { Text(uiState.tagQueryError) },
                    )
                    FlowRow(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        uiState.tagSuggestions.forEach { tag ->
                            FilterChip(
                                selected = tag.isSelected,
                                onClick = { onAction(DiaryAction.TagSelected(tag.id)) },
                                label = { Text(tag.displayLabel) },
                            )
                        }
                        if (uiState.tagOperation is OperationState.Loading) {
                            CircularProgressIndicator()
                        }
                        if (uiState.tagCreateOperation is OperationState.Loading) {
                            CircularProgressIndicator()
                        }
                        if (uiState.canCreateTag) {
                            TextButton(onClick = { onAction(DiaryAction.CreateTagClicked) }) {
                                Text(
                                    text = uiState.createTagLabel,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    if (uiState.canClearTag) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.widthIn(max = 160.dp),
                                text = uiState.selectedTagLabel,
                                color = AppColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            TextButton(onClick = { onAction(DiaryAction.TagCleared) }) {
                                Text(StringKeys.clearTag, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
                when (val operation = uiState.tagOperation) {
                    OperationState.Idle -> Unit
                    OperationState.Loading -> Unit
                    is OperationState.Error -> {
                        Text(operation.error.message, color = AppColors.textSecondary)
                        TextButton(onClick = { onAction(DiaryAction.RetryTagsLoadClicked) }) {
                            Text(StringKeys.retry)
                        }
                    }
                }
                when (val operation = uiState.tagCreateOperation) {
                    OperationState.Idle -> Unit
                    OperationState.Loading -> Unit
                    is OperationState.Error ->
                        Text(operation.error.message, color = AppColors.textSecondary)
                }
            }
            Text(
                text = StringKeys.duration,
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.textSecondary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.isEntryCompleted,
                        onCheckedChange = { onAction(DiaryAction.EntryCompletionChanged(it)) },
                    )
                    Text(StringKeys.alreadyDone)
                }
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                ) {
                    uiState.timerPresetOptions.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = uiState.selectedTimerPreset == option.preset,
                            enabled = option.isEnabled,
                            onClick = { onAction(DiaryAction.TimerPresetSelected(option.preset)) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index,
                                uiState.timerPresetOptions.size
                            ),
                        ) { Text(option.label) }
                    }
                }
            }
            if (uiState.isCustomDurationVisible) {
                OutlinedTextField(
                    value = uiState.customDurationMinutes,
                    enabled = uiState.canChangeDuration,
                    onValueChange = { onAction(DiaryAction.CustomDurationChanged(it)) },
                    label = { Text(StringKeys.custom) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.hasCustomDurationError,
                    supportingText = { Text(uiState.customDurationError) },
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(noteFocusRequester),
                value = uiState.text,
                onValueChange = { onAction(DiaryAction.TextChanged(it)) },
                label = { Text(StringKeys.note) },
                minLines = 3,
                isError = uiState.hasEntryTextError,
                supportingText = { Text(uiState.entryTextError) },
            )
            Text(uiState.timerLabel, color = AppColors.textSecondary)
            when (val operation = uiState.entryOperation) {
                OperationState.Idle -> Unit
                OperationState.Loading -> Unit
                is OperationState.Error -> {
                    Text(operation.error.message, color = AppColors.textSecondary)
                    Button(onClick = { onAction(DiaryAction.RetryEntrySaveClicked) }) {
                        Text(StringKeys.retry)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    enabled = uiState.canCreateOrUpdateEntry,
                    onClick = { onAction(DiaryAction.SaveEntryClicked) },
                ) {
                    Text(uiState.primaryButtonLabel)
                }
                if (uiState.canCancelEditorTimer) {
                    Button(onClick = { onAction(DiaryAction.CancelEditorTimerClicked) }) {
                        Text(StringKeys.cancelTimer)
                    }
                }
            }
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
                entryItems = DiaryOperator.renderEntryItems(entries),
                isEditorVisible = false,
                isHtmlExportAvailable = true
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DiaryEntryEditorPreview() {
    ViewModuleTheme {
        EntryEditor(
            uiState = DiaryOperator.initialState().copy(
                isEditorVisible = true,
                selectedArea = AreaEntry.WORK,
            ),
            onAction = {},
        )
    }
}
