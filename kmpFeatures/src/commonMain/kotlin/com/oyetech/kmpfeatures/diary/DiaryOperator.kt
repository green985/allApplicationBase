package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.entity.EntryEntity
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryAction
import com.oyetech.kmpmodels.ui.state.DiaryUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class DiaryOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
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
            }

            is DiaryAction.AreaSelected -> {
                updateState { copy(selectedArea = action.value) }
            }

            is DiaryAction.TextChanged -> {
                updateState { copy(text = action.value) }
            }

            DiaryAction.AddEntryClicked -> {
                updateState { copy(isEditorVisible = true) }
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

        updateState {
            copy(
                entries = entries + EntryEntity(
                    id = entries.size.toString(),
                    areaId = area.name,
                    text = currentState.text.trim(),
                ),
                isEditorVisible = false,
                selectedArea = null,
                text = "",
            )
        }
    }

    private fun stateFor(offset: Int): DiaryUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + offset)
        return state.value.copy(
            selectedDate = selectedDate,
            dayOffset = offset,
            isTodaySelected = selectedDate == today,
        )
    }

    companion object {
        private fun initialState(): DiaryUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return DiaryUiState(
                selectedDate = today,
                dayOffset = 0,
                isTodaySelected = true,
                entries = listOf(
                    EntryEntity("1", StringKeys.mindArea, StringKeys.sampleMindEntry),
                    EntryEntity("2", StringKeys.bodyArea, StringKeys.sampleBodyEntry),
                ),
                isEditorVisible = false,
                selectedArea = null,
                text = "",
                dayQuote = "",
            )
        }
    }
}
