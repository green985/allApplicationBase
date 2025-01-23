package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerUIEvent.DismissDialog
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerUIEvent.TimerApply
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerUIEvent.TimerClear
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerUIEvent.UpdateSliderPosition
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerUIEvent.UserInteract
import com.oyetech.domain.useCases.TimerOperationUseCase
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class RadioCountTimerViewModel(
    var timerOperationUseCase: TimerOperationUseCase,
    private val dispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
) : ViewModel(
) {
    private val _uiState = MutableStateFlow(RadioCountTimerUIState(isDialogVisible = true))
    val uiState: StateFlow<RadioCountTimerUIState> = _uiState

    private var isUserInteract = false

    init {
        viewModelScope.launch(dispatchers.io) {
            timerOperationUseCase.timerCountStateFlow.collectLatest {
                Timber.d(" timerOperationUseCase.timerCountStateFlow.collectLatest $it")
                if (!isUserInteract) {
                    onEvent(UpdateSliderPosition(it / 60f))
                }
            }
        }
    }

    fun onEvent(event: RadioCountTimerUIEvent) {
        when (event) {
            is DismissDialog -> {
                _uiState.updateState { copy(isDialogVisible = false) }
                isUserInteract = false
            }

            is UpdateSliderPosition -> {
                val sliderTimeText =
                    TimeFunctions.calculateToSecondToMinStringForm((event.position * 60).toLong())
                Timber.d(" sliderTimeText $sliderTimeText")
                _uiState.updateState {
                    copy(
                        sliderPosition = event.position,
                        sliderTimeText = sliderTimeText
                    )
                }
            }

            TimerApply -> {
                // Add any action to perform when TimerApply is triggered
                _uiState.updateState { copy(isDialogVisible = false) }
                timerOperationUseCase.setTimerMin(uiState.value.sliderPosition.toInt())
            }

            TimerClear -> {
                timerOperationUseCase.cancelTimer()
            }

            UserInteract -> {
                isUserInteract = true
            }
        }
    }
}
