package com.oyetech.kmpmodels.ui.event

sealed interface OperatorExampleAction {
    data object RunOperationClicked : OperatorExampleAction
    data object ResetClicked : OperatorExampleAction
    data object BackClicked : OperatorExampleAction
}
