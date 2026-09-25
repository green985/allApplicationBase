package com.oyetech.kmpfeatures.daily

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.ui.event.DailyPagerAction
import com.oyetech.kmpmodels.ui.state.DailyPagerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class DailyPagerOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
) : BaseFeatureOperator<DailyPagerUiState, DailyPagerAction, Nothing>(
    initialState = initialState(),
    operatorScope = operatorScope,
) {
    private var dayOffset = 0

    override fun handleAction(action: DailyPagerAction) {
        when (action) {
            DailyPagerAction.PreviousDayClicked -> {
                dayOffset--
                updateState { stateFor(dayOffset) }
            }

            DailyPagerAction.NextDayClicked -> {
                dayOffset++
                updateState { stateFor(dayOffset) }
            }

            DailyPagerAction.BackClicked -> navigationUseCase.goBack()
            DailyPagerAction.ErrorDismissed -> Unit
        }
    }

    private fun stateFor(offset: Int): DailyPagerUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val selectedDate = LocalDate.fromEpochDays(today.toEpochDays() + offset)
        return DailyPagerUiState(
            selectedDate = selectedDate,
            canGoPrevious = offset > -1,
            canGoNext = offset < 1,
        )
    }

    companion object {
        private fun initialState(): DailyPagerUiState {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return DailyPagerUiState(selectedDate = today)
        }
    }
}
