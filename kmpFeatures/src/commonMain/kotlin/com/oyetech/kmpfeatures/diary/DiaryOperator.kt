package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.postbody.EntryPatchBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryAreaUiState
import com.oyetech.kmpmodels.ui.state.DiaryEntryUiState
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class DiaryOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val diaryEndpointOperation: DiaryEndpointOperation,
) : BaseFeatureOperator<DiaryUiState, DiaryAction, Nothing>(
    initialState = initialState(),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: DiaryAction) {
        when (action) {
            DiaryAction.PreviousDayClicked -> {
                updateState { stateFor(dayOffset - 1) }
            }

            DiaryAction.NextDayClicked -> {
                updateState { stateFor(dayOffset + 1) }
            }

            is DiaryAction.QuoteChanged -> {
                updateState { copy(dayQuote = action.value) }
                launch {
                    delay(400)
                    if (state.value.dayQuote != action.value) return@launch
                    diaryEndpointOperation.updateQuote(
                        date = state.value.selectedDate.toString(),
                        quote = action.value,
                    ).onFailure { error ->
                        updateState {
                            copy(
                                isError = true,
                                errorMessage = error.message.orEmpty(),
                            )
                        }
                    }
                }
            }

            is DiaryAction.AreaSelected -> {
                updateState { copy(selectedArea = action.value) }
            }

            is DiaryAction.TextChanged -> {
                updateState { copy(text = action.value) }
            }

            is DiaryAction.EntryEditClicked -> {
                val entry = state.value.entries.firstOrNull { it.id == action.entryId } ?: return
                updateState {
                    copy(
                        isEditorVisible = true,
                        editingEntryId = entry.id,
                        selectedArea = AreaEntry.valueOf(entry.areaId),
                        text = entry.text,
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
                    )
                }
            }

            DiaryAction.EntryDialogDismissed -> {
                if (state.value.selectedArea == null && state.value.text.isBlank()) {
                    updateState { copy(isEditorVisible = false) }
                } else {
                    saveEntry()
                }
            }

            DiaryAction.SaveEntryClicked -> saveEntry()
            DiaryAction.BackClicked -> navigationUseCase.goBack()
            DiaryAction.ErrorDismissed -> Unit
        }
    }

    private fun saveEntry() {
        val currentState = state.value
        val area = currentState.selectedArea
        if (area == null || currentState.text.isBlank()) return

        val updatedEntries = if (currentState.editingEntryId == null) {
            currentState.entries + EntryEntity(
                id = currentState.entries.size.toString(),
                areaId = area.name,
                text = currentState.text.trim(),
                createdBy = StringKeys.adminUsername,
                createdAt = Clock.System.now(),
            )
        } else {
            currentState.entries.map { entry ->
                if (entry.id == currentState.editingEntryId) {
                    entry.copy(
                        areaId = area.name,
                        text = currentState.text.trim(),
                        updatedAt = Clock.System.now(),
                    )
                } else {
                    entry
                }
            }
        }
        val editingEntryId = currentState.editingEntryId
        updateState {
            copy(
                entries = updatedEntries,
                entryItems = renderEntryItems(updatedEntries),
                isEditorVisible = false,
                editingEntryId = null,
                selectedArea = null,
                text = "",
            )
        }
        launch {
            val result = if (editingEntryId == null) {
                diaryEndpointOperation.createEntry(
                    EntryPostBody(
                        areaId = area.name,
                        text = currentState.text.trim(),
                        occurredAt = currentState.selectedDate.toString(),
                    ),
                )
            } else {
                diaryEndpointOperation.updateEntry(
                    entryId = editingEntryId,
                    request = EntryPatchBody(
                        areaId = area.name,
                        text = currentState.text.trim(),
                        occurredAt = currentState.selectedDate.toString(),
                    ),
                )
            }
            result.onFailure { error ->
                updateState {
                    copy(
                        isError = true,
                        errorMessage = error.message.orEmpty(),
                    )
                }
            }
        }
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
