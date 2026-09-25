package com.oyetech.kmpfeatures.example

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.ui.event.OperatorExampleAction
import com.oyetech.kmpmodels.ui.state.OperatorExampleUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

class OperatorExampleOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseFeatureOperator<OperatorExampleUiState, OperatorExampleAction, Nothing>(
    initialState = OperatorExampleUiState(),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: OperatorExampleAction) {
        when (action) {
            OperatorExampleAction.RunOperationClicked -> runOperation()
            OperatorExampleAction.ResetClicked -> reset()
            OperatorExampleAction.BackClicked -> navigationUseCase.goBack()
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
            snackbarDelegate.triggerSnackbarState("Operation completed")
        }
    }

    private fun reset() {
        updateState { OperatorExampleUiState() }
    }
}
