package com.oyetech.kmpmodels.ui.event

sealed interface HomeAction {
    data object AdminLoginClicked : HomeAction
    data object OperatorExampleClicked : HomeAction
    data object ErrorDismissed : HomeAction
}
