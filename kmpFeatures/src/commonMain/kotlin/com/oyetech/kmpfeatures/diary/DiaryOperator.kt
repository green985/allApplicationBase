package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpfeatures.operator.cancelPendingSave
import com.oyetech.kmpfeatures.operator.scheduleDebouncedSave
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.postbody.EntryPatchBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.response.EntryResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import com.oyetech.kmpmodels.ui.state.OperationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

class DiaryOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val diaryEndpointOperation: DiaryEndpointOperation,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseFeatureOperator<DiaryUiState, DiaryAction, Nothing>(
    initialState = initialState(),
    operatorScope = operatorScope,
) {
    private var quoteAutosaveJob: Job? = null
    private var entryAutosaveJob: Job? = null

    init {
        loadQuoteForSelectedDate()
    }

    override fun handleAction(action: DiaryAction) {
        when (action) {
            DiaryAction.PreviousDayClicked -> {
                selectDay(state.value.dayOffset - 1)
            }

            DiaryAction.NextDayClicked -> {
                selectDay(state.value.dayOffset + 1)
            }

            is DiaryAction.QuoteChanged -> {
                updateState {
                    copy(
                        dayQuote = action.value,
                        quoteIsDirty = true,
                        quoteRevision = quoteRevision + 1,
                    )
                }
                scheduleQuoteSave()
            }

            is DiaryAction.AreaSelected -> {
                updateState {
                    copy(
                        selectedArea = action.value,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    )
                }
                scheduleEntrySave()
            }

            is DiaryAction.TextChanged -> {
                updateState {
                    copy(
                        text = action.value,
                        entryIsDirty = true,
                        entryRevision = entryRevision + 1,
                    )
                }
                scheduleEntrySave()
            }

            is DiaryAction.EntryEditClicked -> {
                val entry = state.value.entries.firstOrNull { it.id == action.entryId } ?: return
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = entry.id,
                        selectedArea = AreaEntry.valueOf(entry.areaId),
                        text = entry.text,
                        entryIsDirty = false,
                    )
                }
            }

            DiaryAction.AddEntryClicked -> {
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = null,
                        selectedArea = null,
                        text = "",
                        entryIsDirty = false,
                    )
                }
            }

            DiaryAction.EntryDialogDismissed -> {
                val currentState = state.value
                if (currentState.selectedArea == null && currentState.text.isNotBlank()) {
                    snackbarDelegate.triggerSnackbarState(
                        message = StringKeys.selectAreaRequired,
                    )
                } else if (currentState.selectedArea != null) {
                    saveEntry(closeEditor = true, allowIdleArea = false)
                } else {
                    updateState {
                        copy(
                            isEditorVisible = false,
                            editingEntryId = null,
                            selectedArea = null,
                            text = "",
                            entryIsDirty = false,
                            entryOperation = OperationState.Idle,
                        )
                    }
                }
            }

            DiaryAction.SaveEntryClicked -> saveEntry(
                closeEditor = true,
                allowIdleArea = false,
            )
            DiaryAction.RetryQuoteSaveClicked -> scheduleQuoteSave()
            DiaryAction.RetryEntrySaveClicked -> scheduleEntrySave()
            DiaryAction.BackClicked -> navigationUseCase.goBack()
            DiaryAction.ErrorDismissed -> updateState {
                copy(
                    quoteOperation = OperationState.Idle,
                    entryOperation = OperationState.Idle,
                )
            }
        }
    }

    private fun scheduleQuoteSave() {
        quoteAutosaveJob = operatorScope.scheduleDebouncedSave(
            previousJob = quoteAutosaveJob,
            canSave = {
                val currentState = state.value
                currentState.quoteIsDirty &&
                        currentState.quoteOperation !is OperationState.Loading &&
                        currentState.dayQuote.isNotBlank()
            },
            onSave = { saveQuoteSnapshot() },
        )
    }

    private fun selectDay(offset: Int) {
        quoteAutosaveJob.cancelPendingSave()
        updateState {
            stateFor(offset).copy(
                dayQuote = "",
                quoteIsDirty = false,
                quoteOperation = OperationState.Idle,
            )
        }
        loadQuoteForSelectedDate()
    }

    private fun loadQuoteForSelectedDate() {
        val dateSnapshot = state.value.selectedDate
        executeOperation(
            onStart = {
                copy(quoteOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.getQuote(dateSnapshot.toString())
            },
            onSuccess = { response ->
                if (selectedDate == dateSnapshot && !quoteIsDirty) {
                    copy(
                        dayQuote = response.quote,
                        quoteOperation = OperationState.Idle,
                    )
                } else {
                    copy(quoteOperation = OperationState.Idle)
                }
            },
            onError = { error ->
                copy(
                    dayQuote = "",
                    quoteOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun saveQuoteSnapshot() {
        quoteAutosaveJob.cancelPendingSave()
        val snapshot = state.value
        val revision = snapshot.quoteRevision
        val quoteSnapshot = snapshot.dayQuote
        val dateSnapshot = snapshot.selectedDate

        executeOperation(
            onStart = {
                copy(quoteOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.updateQuote(
                    date = dateSnapshot.toString(),
                    quote = quoteSnapshot,
                )
            },
            onSuccess = { response ->
                val nextState = if (selectedDate != dateSnapshot) {
                    copy(quoteOperation = OperationState.Idle)
                } else {
                    copy(
                        dayQuote = if (dayQuote == quoteSnapshot) response.quote else dayQuote,
                        quoteIsDirty = quoteRevision != revision,
                        quoteOperation = OperationState.Idle,
                    )
                }
                if (quoteRevision != revision) scheduleQuoteSave()
                nextState
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                    actionLabel = "Retry",
                    onAction = { dispatch(DiaryAction.RetryQuoteSaveClicked) },
                )
                copy(
                    quoteIsDirty = true,
                    quoteOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun scheduleEntrySave() {
        entryAutosaveJob = operatorScope.scheduleDebouncedSave(
            previousJob = entryAutosaveJob,
            canSave = {
                val currentState = state.value
                currentState.entryIsDirty &&
                        currentState.entryOperation !is OperationState.Loading &&
                        (currentState.selectedArea != null || currentState.text.isNotBlank())
            },
            onSave = { saveEntry(closeEditor = false, allowIdleArea = true) },
        )
    }

    private fun saveEntry(
        closeEditor: Boolean,
        allowIdleArea: Boolean,
    ) {
        val currentState = state.value
        val area = currentState.selectedArea
        if (currentState.entryOperation is OperationState.Loading) {
            return
        }

        entryAutosaveJob.cancelPendingSave()
        val text = currentState.text.trim()
        if (area == null && !allowIdleArea) {
            snackbarDelegate.triggerSnackbarState(
                message = StringKeys.selectAreaRequired,
            )
            return
        }
        if (area == null && text.isBlank()) {
            return
        }
        val areaForRequest = area ?: AreaEntry.IDLE
        val revision = currentState.entryRevision
        val editingEntryId = currentState.editingEntryId
        val dateSnapshot = currentState.selectedDate

        if (editingEntryId == null) {
            executeOperation(
                onStart = {
                    copy(entryOperation = OperationState.Loading)
                },
                operation = {
                    diaryEndpointOperation.createEntry(
                        EntryPostBody(
                            areaId = areaForRequest.name,
                            text = text,
                            entryDate = dateSnapshot.toString(),
                        ),
                    )
                },
                onSuccess = { response ->
                    val hasNewInput = entryRevision != revision
                    if (selectedDate != dateSnapshot) {
                        copy(entryOperation = OperationState.Idle)
                    } else {
                        val serverEntry = entryEntity(response)
                        val nextEntries = entries + serverEntry
                        if (hasNewInput) scheduleEntrySave()
                        copy(
                            entries = nextEntries,
                            entryItems = renderEntryItems(nextEntries),
                            editingEntryId = response.id,
                            entryIsDirty = hasNewInput,
                            entryOperation = OperationState.Idle,
                            isEditorVisible = if (closeEditor && !hasNewInput) {
                                false
                            } else {
                                true
                            },
                            selectedArea = if (closeEditor && !hasNewInput) {
                                null
                            } else {
                                areaForRequest
                            },
                            text = if (closeEditor && !hasNewInput) "" else text,
                        )
                    }
                },
                onError = { error ->
                    snackbarDelegate.triggerSnackbarState(
                        message = error.message,
                        actionLabel = "Retry",
                        onAction = { dispatch(DiaryAction.RetryEntrySaveClicked) },
                    )
                    copy(
                        entryIsDirty = true,
                        entryOperation = OperationState.Error(error),
                    )
                },
            )
            return
        }

        val updatedEntries = currentState.entries.map { entry ->
            if (entry.id == editingEntryId) {
                entry.copy(
                    areaId = areaForRequest.name,
                    text = text,
                    updatedAt = Clock.System.now(),
                )
            } else {
                entry
            }
        }
        updateState {
            copy(
                entries = updatedEntries,
                entryItems = renderEntryItems(updatedEntries),
                isEditorVisible = if (closeEditor) false else currentState.isEditorVisible,
                editingEntryId = if (closeEditor) null else editingEntryId,
                selectedArea = if (closeEditor) null else area,
                text = if (closeEditor) "" else text,
                entryOperation = OperationState.Loading,
            )
        }
        executeOperation(
            onStart = {
                copy(entryOperation = OperationState.Loading)
            },
            operation = {
                diaryEndpointOperation.updateEntry(
                    entryId = editingEntryId,
                    request = EntryPatchBody(
                        areaId = areaForRequest.name,
                        text = text,
                        occurredAt = dateSnapshot.toString(),
                    ),
                )
            },
            onSuccess = {
                if (entryRevision != revision) scheduleEntrySave()
                copy(
                    entryIsDirty = entryRevision != revision,
                    entryOperation = OperationState.Idle,
                )
            },
            onError = { error ->
                snackbarDelegate.triggerSnackbarState(
                    message = error.message,
                    actionLabel = "Retry",
                    onAction = { dispatch(DiaryAction.RetryEntrySaveClicked) },
                )
                copy(
                    entryIsDirty = true,
                    entryOperation = OperationState.Error(error),
                )
            },
        )
    }

    private fun stateFor(offset: Int): DiaryUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + offset)
        return state.value.copy(
            selectedDate = selectedDate,
            dayOffset = offset,
            isTodaySelected = selectedDate == today,
            selectedDateLabel = formatDate(selectedDate),
        )
    }

    companion object {
        private fun initialState(): DiaryUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val entries = listOf(
                EntryEntity(
                    id = "1",
                    areaId = AreaEntry.MIND.name,
                    text = StringKeys.sampleMindEntry,
                    createdBy = StringKeys.adminUsername,
                    createdAt = Clock.System.now(),
                ),
                EntryEntity(
                    id = "2",
                    areaId = AreaEntry.BODY.name,
                    text = StringKeys.sampleBodyEntry,
                    createdBy = StringKeys.adminUsername,
                    createdAt = Clock.System.now(),
                ),
            )
            return DiaryUiState(
                selectedDate = today,
                dayOffset = 0,
                isTodaySelected = true,
                entries = entries,
                selectedDateLabel = formatDate(today),
                entryItems = renderEntryItems(entries),
                areaOptions = areaOptions(),
                isEditorVisible = false,
                editingEntryId = null,
                selectedArea = null,
                text = "",
                dayQuote = "",
            )
        }

        private fun renderEntryItems(entries: List<EntryEntity>): List<DiaryEntryUiState> =
            entries.map { entry ->
                DiaryEntryUiState(
                    id = entry.id,
                    areaLabel = areaLabel(entry.areaId),
                    createdBy = entry.createdBy,
                    timeLabel = entry.createdAt?.let(::formatTime).orEmpty(),
                    text = entry.text,
                )
            }

        private fun entryEntity(response: EntryResponse): EntryEntity =
            EntryEntity(
                id = response.id,
                areaId = response.areaId,
                text = response.text,
                entryDate = response.entryDate,
                createdBy = response.createdBy.orEmpty(),
                createdAt = response.createdAt?.let(Instant::parse),
                updatedAt = response.updatedAt?.let(Instant::parse),
            )

        private fun areaOptions(): List<DiaryAreaUiState> =
            AreaEntry.entries.map { area ->
                DiaryAreaUiState(
                    area = area,
                    label = areaLabel(area.name),
                )
            }

        private fun formatDate(date: LocalDate): String =
            "${date.day} ${StringKeys.turkishMonths[date.monthNumber - 1]} ${date.year}"

        private fun formatTime(instant: kotlin.time.Instant): String {
            val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time
            return "${localTime.hour.toString().padStart(2, '0')}:${
                localTime.minute.toString().padStart(2, '0')
            }"
        }

        private fun areaLabel(areaId: String): String = when (areaId) {
            AreaEntry.IDLE.name -> StringKeys.idleArea
            AreaEntry.WORK.name -> StringKeys.workArea
            AreaEntry.BODY.name -> StringKeys.bodyArea
            AreaEntry.HEALTH.name -> StringKeys.healthArea
            AreaEntry.MIND.name -> StringKeys.mindArea
            AreaEntry.CHARACTER.name -> StringKeys.characterArea
            AreaEntry.PEOPLE.name -> StringKeys.peopleArea
            AreaEntry.LIFE.name -> StringKeys.lifeArea
            else -> areaId
        }
    }
}
