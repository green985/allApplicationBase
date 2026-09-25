package com.oyetech.kmpfeatures.example

import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.ui.event.OperatorExampleAction
import com.oyetech.kmpmodels.ui.event.OperatorExampleEffect
import com.oyetech.kmpmodels.ui.state.OperatorExampleUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

class OperatorExampleOperator(
    operatorScope: CoroutineScope,
) : BaseFeatureOperator<OperatorExampleUiState, OperatorExampleAction, OperatorExampleEffect>(
    initialState = OperatorExampleUiState(),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: OperatorExampleAction) {
        when (action) {
            OperatorExampleAction.RunOperationClicked -> runOperation()
            OperatorExampleAction.ResetClicked -> reset()
            OperatorExampleAction.BackClicked -> emitEffect(OperatorExampleEffect.NavigateBack)
        }
    }

    private fun runOperation() {
        if (state.value.isLoading) return

        updateState { copy(isLoading = true) }
        launch {
            delay(500)
            updateState {
                copy(
                    operationCount = operationCount + 1,
                    isLoading = false,
                )
            }
            emitEffect(OperatorExampleEffect.ShowMessage("Operation completed"))
        }
    }

    private fun reset() {
        updateState { OperatorExampleUiState() }
    }
}
