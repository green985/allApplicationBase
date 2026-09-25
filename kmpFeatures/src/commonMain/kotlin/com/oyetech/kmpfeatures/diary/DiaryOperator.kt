package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.entity.EntryEntity
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
    private var dayOffset = 0
    private var selectedArea: AreaEntry? = null
    private var text = ""
    private var dayQuote = ""
    private var isEditorVisible = false
    private val entries = mutableListOf(
        EntryEntity("1", "Zihin", "Bugün için küçük bir başlangıç yaptım."),
        EntryEntity("2", "Beden", "Kısa bir yürüyüş iyi geldi."),
    )

    override fun handleAction(action: DiaryAction) {
        when (action) {
            DiaryAction.PreviousDayClicked -> {
                dayOffset--
                updateState { stateFor(dayOffset) }
            }

            DiaryAction.NextDayClicked -> {
                dayOffset++
                updateState { stateFor(dayOffset) }
            }

            is DiaryAction.QuoteChanged -> {
                dayQuote = action.value
                updateState { copy(dayQuote = dayQuote) }
            }

            is DiaryAction.AreaSelected -> {
                selectedArea = action.value
                updateState { copy(selectedArea = selectedArea) }
            }

            is DiaryAction.TextChanged -> {
                text = action.value
                updateState { copy(text = text) }
            }

            DiaryAction.AddEntryClicked -> {
                isEditorVisible = true
                updateState { copy(isEditorVisible = true) }
            }

            DiaryAction.EntryDialogDismissed -> {
                if (selectedArea == null && text.isBlank()) {
                    isEditorVisible = false
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
        val area = selectedArea
        if (area == null || text.isBlank()) return

        entries.add(EntryEntity(entries.size.toString(), area.name, text.trim()))
        selectedArea = null
        text = ""
        isEditorVisible = false
        updateState {
            copy(
                entries = entries.toList(),
                isEditorVisible = false,
                selectedArea = null,
                text = "",
            )
        }
    }

    private fun stateFor(offset: Int): DiaryUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + offset)
        return DiaryUiState(
            selectedDate = selectedDate,
            isTodaySelected = selectedDate == today,
            entries = entries.toList(),
            isEditorVisible = isEditorVisible,
            selectedArea = selectedArea,
            text = text,
            dayQuote = dayQuote,
        )
    }

    companion object {
        private fun initialState(): DiaryUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return DiaryUiState(
                selectedDate = today,
                isTodaySelected = true,
                entries = listOf(
                    EntryEntity("1", "Zihin", "Bugün için küçük bir başlangıç yaptım."),
                    EntryEntity("2", "Beden", "Kısa bir yürüyüş iyi geldi."),
                ),
                isEditorVisible = false,
                selectedArea = null,
                text = "",
                dayQuote = "",
            )
        }
    }
}
