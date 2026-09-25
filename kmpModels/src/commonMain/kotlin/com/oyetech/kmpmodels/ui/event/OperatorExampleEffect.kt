package com.oyetech.kmpmodels.ui.event

sealed interface OperatorExampleEffect {
    data class ShowMessage(val message: String) : OperatorExampleEffect
    data object NavigateBack : OperatorExampleEffect
}
